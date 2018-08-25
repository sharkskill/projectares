package tc.oc.pgm.uhc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.api.util.Permissions;
import tc.oc.commons.bukkit.chat.BukkitSound;
import tc.oc.commons.bukkit.chat.PlayerComponent;
import tc.oc.commons.bukkit.nick.IdentityProvider;
import tc.oc.commons.core.chat.Audience;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.chat.MultiAudience;
import tc.oc.commons.core.util.Pair;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.map.PGMMap;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Stream;

@ListenerScope(MatchScope.LOADED)
public class XrayDetectionMatchModule extends MatchModule implements Listener {

    private final PGMMap map;
    private static final MaterialBundle[] MATERIALS = new MaterialBundle[]{
            MaterialBundle.of(Material.DIAMOND_ORE, 16, "diamonds"),
            MaterialBundle.of(Material.MOB_SPAWNER, 2, "mob spawners"),
    };
    private final Audiences audiences;
    private final IdentityProvider identityProvider;
    private Table<UUID, Material, Integer> xray = HashBasedTable.create();

    @Inject
    XrayDetectionMatchModule(PGMMap map, Audiences audiences, IdentityProvider identityProvider) {
        this.map = map;
        this.audiences = audiences;
        this.identityProvider = identityProvider;
    }

    private Audience alertAudience() {
        return (MultiAudience) () -> Stream.concat(
                Stream.of(audiences.console()),
                match.getDefaultParty().audiences().filter(a -> ((MatchPlayer) a).getBukkit().hasPermission(Permissions.STAFF))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBread(BlockBreakEvent event) {
        for (MaterialBundle material : MATERIALS) {
            if (material.first == event.getBlock().getType()) {
                UUID uuid = event.getPlayer().getUniqueId();
                int count = xray.contains(uuid, material.first) ? xray.get(uuid, material.first) + 1 : 1;
                xray.put(uuid, material.first, count);
                if (count >= material.second) {
                    Audience alert = alertAudience();
                    alert.sendMessage(
                            new Component(ChatColor.AQUA).translate(
                                    "xray.alert",
                                    new PlayerComponent(identityProvider.currentIdentity(event.getPlayer())),
                                    new Component(count, determineSeverity(count, material.second)),
                                    new Component(material.human, ChatColor.GRAY)
                            )
                    );
                    alert.playSound(new BukkitSound(Sound.ENTITY_PLAYER_LEVELUP, 1, .5f));
                }
                break;
            }
        }
    }

    @Override
    public boolean shouldLoad() {
        return super.shouldLoad() && map.getDocument().gamemode().contains(MapDoc.Gamemode.uhc);
    }

    private ChatColor determineSeverity(int count, int base) {
        switch (Math.floorDiv(count, base)) {
            case 1:
                return ChatColor.GREEN;
            case 2:
                return ChatColor.YELLOW;
            case 3:
                return ChatColor.GOLD;
            case 4:
                return ChatColor.RED;
            default:
                return ChatColor.DARK_RED;
        }
    }

    private static final class MaterialBundle extends Pair<Material, Integer> {
        public final String human;

        public MaterialBundle(Material first, Integer second, String human) {
            super(first, second);
            this.human = human;
        }

        public static MaterialBundle of(Material material, Integer integer, String human) {
            return new MaterialBundle(material, integer, human);
        }
    }
}
