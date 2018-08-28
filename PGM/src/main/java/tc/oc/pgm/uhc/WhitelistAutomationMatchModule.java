package tc.oc.pgm.uhc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.api.util.Permissions;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScheduler;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.time.Duration;

@ListenerScope(MatchScope.LOADED)
public class WhitelistAutomationMatchModule extends MatchModule implements Listener {

    private final MatchScheduler scheduler;

    @Inject public WhitelistAutomationMatchModule(MatchScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBegin(MatchBeginEvent event) {
        for (MatchPlayer player : event.getMatch().getParticipatingPlayers()) {
            if (!player.getBukkit().isWhitelisted()) {
                player.getBukkit().setWhitelisted(true);
                player.sendMessage(ChatColor.YELLOW + "Whitelisted.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEliminate(MatchPlayerDeathEvent event) {
        if (event.getMatch().getParticipatingPlayers().size() > 10 && !event.getVictim().getBukkit().hasPermission(Permissions.STAFF)) {
            scheduler.createDelayedTask(Duration.ofSeconds(10), () -> {
                if (event.getVictim().isParticipating()) return;
                event.getVictim().getBukkit().setWhitelisted(false);
                event.getVictim().sendMessage(ChatColor.YELLOW + "Kicking in 15 seconds");
                event.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(15), () -> event.getVictim().getBukkit().kickPlayer("Thanks for playing"));
            });
        }
    }
}
