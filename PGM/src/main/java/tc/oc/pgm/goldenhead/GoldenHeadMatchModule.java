package tc.oc.pgm.goldenhead;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.api.util.Permissions;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerBlockTransformEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ListenerScope(MatchScope.RUNNING)
public class GoldenHeadMatchModule extends MatchModule implements Listener {

    @Inject private Server server;

    private static String GOLDEN_HEAD_DISPLAY = ChatColor.BOLD.toString() + ChatColor.AQUA + "Golden Head";

    public GoldenHeadMatchModule(Match match) {
        super(match);
    }

    @Override
    public void enable() {
        super.enable();

        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta newMeta = goldenHead.getItemMeta();
        newMeta.setDisplayName(GOLDEN_HEAD_DISPLAY);
        goldenHead.setItemMeta(newMeta);

        ShapedRecipe recipe = new ShapedRecipe(goldenHead);

        recipe.shape("GGG", "GHG", "GGG");

        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('H', new MaterialData(Material.SKULL_ITEM, (byte)3));

        server.addRecipe(recipe);
    }

    @Override
    public void disable() {
        // Recipe changes affect all worlds on the server, so we make changes at match start/end
        // to avoid interfering with adjacent matches. If we wait until unload() to reset them,
        // the next match would already be loaded.
        server.resetRecipes();
        super.disable();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta() && GOLDEN_HEAD_DISPLAY.equals(item.getItemMeta().getDisplayName())) {
            event.getActor().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }

    private Map<UUID, Integer> xray = new HashMap<>();

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
                    player.sendMessage(ChatColor.RED + "Warning: " + event.getPlayer().getDisplayName() + " has mined " + xray.get(uuid) + " diamonds");
                }
            }
        }
    }

    private Map<UUID, Duration> fights = new HashMap<UUID, Duration>();

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onFight(EntityDamageByEntityEvent event) {
//        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
//            if (!fights.containsKey(event.getDamager().getUniqueId())) {
//                if (!fights.containsKey(event.getEntity().getUniqueId())) {
//                    fights.put(event.getDamager().getUniqueId(), match.runningTime());
//                    fights.put(event.getEntity().getUniqueId(), match.runningTime());
//                    for (MatchPlayer player : match.getObservingPlayers()) {
//                        player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                    }
//                    return;
//                }
//                Duration difference = match.runningTime().minus(fights.get(event.getEntity().getUniqueId()));
//                if (Comparables.greaterOrEqual(difference, Duration.ofMinutes(1))) {
//                    for (MatchPlayer player : match.getObservingPlayers()) {
//                        player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                    }
//                }
//                return;
//            }
//            Duration difference = match.runningTime().minus(fights.get(event.getEntity().getUniqueId()));
//            if (Comparables.greaterOrEqual(difference, Duration.ofMinutes(1))) {
//                for (MatchPlayer player : match.getObservingPlayers()) {
//                    player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                }
//                return;
//            }
//        }
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation();
        location.getBlock().setType(Material.NETHER_FENCE);
        location.add(0, 1, 0);
        location.getBlock().setType(Material.SKULL);
        location.getBlock().setData((byte) 1);
        Skull skull = (Skull) location.getBlock().getState();
        skull.setSkullType(SkullType.PLAYER);
        skull.setRotation(BlockFace.NORTH);
        skull.setOwner(event.getEntity().getName());
        skull.update();
    }
}
