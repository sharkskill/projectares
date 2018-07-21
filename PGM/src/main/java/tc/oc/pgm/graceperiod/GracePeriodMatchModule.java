package tc.oc.pgm.graceperiod;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import java.time.Duration;

@ListenerScope(MatchScope.RUNNING)
public class GracePeriodMatchModule extends MatchModule implements Listener {

    private final Duration duration;

    public GracePeriodMatchModule(Match match, Duration duration) {
        super(match);
        this.duration = duration;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow) && match.runningTime().compareTo(this.duration) < 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityCombustByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getCombuster() instanceof Player && event.getActor() != event.getCombuster() && match.runningTime().compareTo(this.duration) < 0) {
            event.setCancelled(true);
        }
    }
}
