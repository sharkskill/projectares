package tc.oc.pgm.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import java.time.Duration;
import java.util.List;

@ListenerScope(MatchScope.RUNNING)
public class KillChestMatchModule extends MatchModule implements Listener {

    private final Duration explodeAfter;

    public KillChestMatchModule(Match match, Duration explodeAfter) {
        super(match);
        this.explodeAfter = explodeAfter;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        Location killedLocation = event.getEntity().getLocation();

        killedLocation.getBlock().setType(Material.CHEST);

        killedLocation = killedLocation.add(0, 0, -1);
        killedLocation.getBlock().setType(Material.CHEST);

        Chest chest = (Chest) killedLocation.getBlock().getState();
        drops.forEach(drop -> chest.getBlockInventory().addItem(drop));
        drops.clear();

        if (this.explodeAfter != null) {
            getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(this.explodeAfter, () -> {
                TNTPrimed tnt = (TNTPrimed) getMatch().getWorld().spawnEntity(chest.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setYield(2);

                //Blow up the stuff as well
                TNTPrimed tnt2 = (TNTPrimed) getMatch().getWorld().spawnEntity(chest.getLocation(), EntityType.PRIMED_TNT);
                tnt2.setFuseTicks(0);
                tnt2.setYield(2);
            });
        }
    }
}
