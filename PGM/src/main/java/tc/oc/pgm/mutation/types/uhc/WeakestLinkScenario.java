package tc.oc.pgm.mutation.types.uhc;

import com.google.common.math.DoubleMath;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WeakestLinkScenario extends UHCMutation.Impl {
    final private static Duration KILL_TIME = Duration.ofMinutes(5);

    public WeakestLinkScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    private Duration lastKillTime;
    private Task weakestLinkTask;

    public Object[] componentObjects() {
        return new Object[]{KILL_TIME.toMinutes()};
    }

    private boolean isKillTime(Duration durationSinceLastKill) {
        return Comparables.greaterOrEqual(durationSinceLastKill, KILL_TIME);
    }

    private boolean isBroadcastTime(Duration durationSinceLastKill) {
        long minutes = durationSinceLastKill.toMinutes();
        long seconds = durationSinceLastKill.getSeconds() - (minutes * 60);

        return (minutes <= 5 &&
                (minutes == 5 && seconds == 0  ||
                        minutes == 4 && seconds == 0  ||
                        minutes == 3 && seconds == 0  ||
                        minutes == 2 && seconds == 0  ||
                        minutes == 1 && seconds == 0  ||
                        minutes == 0 && seconds == 45 ||
                        minutes == 0 && seconds == 30 ||
                        minutes == 0 && seconds == 15 ||
                        minutes == 0 && seconds == 10 ||
                        minutes == 0 && seconds == 5  ||
                        minutes == 0 && seconds == 4  ||
                        minutes == 0 && seconds == 3  ||
                        minutes == 0 && seconds == 2  ||
                        minutes == 0 && seconds == 1));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(MatchBeginEvent event) {
        lastKillTime = match().runningTime();
        if (weakestLinkTask != null) {
            return;
        }
        weakestLinkTask = match().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofSeconds(1), () -> {
            Duration durationSinceLastKill = match().runningTime().minus(lastKillTime);
            if (isBroadcastTime(KILL_TIME.minus(durationSinceLastKill))) {
                match().sendMessage(message("mutation.type.weakestlink.warning", ChatColor.RED, PeriodFormats.formatColonsLong(KILL_TIME.minus(durationSinceLastKill))));
            }
            if (isKillTime(durationSinceLastKill)) {
                double lowestHealth = 20;
                for (MatchPlayer player : match().getParticipatingPlayers()) {
                    double health = player.getBukkit().getHealth();
                    if (health < lowestHealth) {
                        lowestHealth = health;
                    }
                }

                if (lowestHealth == 20) {
                    match().sendMessage(message("mutation.type.weakestlink.safe"));
                } else {
                    match().sendMessage(message("mutation.type.weakestlink.death"));
                    for (MatchPlayer player : match().getParticipatingPlayers()) {
                        if (DoubleMath.fuzzyEquals(lowestHealth, player.getBukkit().getHealth(), 0.00005)) {
                            damage(player.getBukkit(), 0);
                        }
                    }
                }
                lastKillTime = match().runningTime();
            }
        });
    }

    @Override
    public void enable() {
        super.enable();

        if (match().hasStarted()) {
            lastKillTime = match().runningTime();
            if (weakestLinkTask != null) {
                return;
            }
            weakestLinkTask = match().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofSeconds(1), () -> {
                Duration durationSinceLastKill = match().runningTime().minus(lastKillTime);
                if (isBroadcastTime(KILL_TIME.minus(durationSinceLastKill))) {
                    match().sendMessage(message("mutation.type.weakestlink.warning", ChatColor.RED, PeriodFormats.formatColonsLong(KILL_TIME.minus(durationSinceLastKill))));
                }
                if (isKillTime(durationSinceLastKill)) {
                    double lowestHealth = 20;
                    for (MatchPlayer player : match().getParticipatingPlayers()) {
                        double health = player.getBukkit().getHealth();
                        if (health < lowestHealth) {
                            lowestHealth = health;
                        }
                    }

                    if (lowestHealth == 20) {
                        match().sendMessage(message("mutation.type.weakestlink.safe"));
                    } else {
                        match().sendMessage(message("mutation.type.weakestlink.death"));
                        for (MatchPlayer player : match().getParticipatingPlayers()) {
                            if (DoubleMath.fuzzyEquals(lowestHealth, player.getBukkit().getHealth(), 0.00005)) {
                                damage(player.getBukkit(), 0);
                            }
                        }
                    }
                    lastKillTime = match().runningTime();
                }
            });
        }
    }

    @Override
    public void disable() {
        super.disable();

        if (weakestLinkTask != null) {
            weakestLinkTask.cancel();
        }
    }

}
