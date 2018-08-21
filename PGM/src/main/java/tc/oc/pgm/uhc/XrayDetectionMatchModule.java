package tc.oc.pgm.uhc;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.api.util.Permissions;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.map.PGMMap;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ListenerScope(MatchScope.LOADED)
public class XrayDetectionMatchModule extends MatchModule implements Listener {

    private final PGMMap map;
    private Map<UUID, Integer> xray = new HashMap<>();

    @Inject
    XrayDetectionMatchModule(PGMMap map) {
        this.map = map;
    }

    @Override
    public boolean shouldLoad() {
        return map.getDocument().gamemode().contains(MapDoc.Gamemode.uhc);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.DIAMOND_ORE)) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        xray.put(uuid, xray.containsKey(uuid) ? xray.get(uuid) + 1 : 1);
        if (xray.get(uuid) >= 16) {
            for (MatchPlayer player : match.getObservingPlayers()) {
                if (player.getBukkit().hasPermission(Permissions.STAFF)) {
                    player.sendMessage(org.bukkit.ChatColor.RED + "Warning: " + event.getPlayer().getDisplayName() + " has mined " + xray.get(uuid) + " diamonds");
                    player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 0, .5f);
                }
            }
        }
    }
}
