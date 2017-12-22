package tc.oc.pgm.achievements.type.oneoff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.achievements.event.OneOffAchievementEvent;
import tc.oc.pgm.achievements.type.OneOffAchievement;
import tc.oc.pgm.events.MatchEndEvent;
import tc.oc.pgm.match.MatchPlayer;

import java.time.Duration;

public class TwoHourMatchAchievement extends OneOffAchievement implements Listener {

    public TwoHourMatchAchievement() {
        super("TwoHourMatch", ChatColor.AQUA + ChatColor.BOLD.toString() + "Two Hour Match", "Play and finish in a match that lasts longer than 2 hours.", "Completed a 2 hour long match.");
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Duration duration = event.getMatch().getLength();
        if (duration.getSeconds() >= 7200) {
            for (MatchPlayer matchPlayer : event.getMatch().getParticipatingPlayers()) {
                Player player = matchPlayer.getBukkit();
                OneOffAchievementEvent oneOffAchievementEvent = new OneOffAchievementEvent(player, this, false);
                Bukkit.getPluginManager().callEvent(oneOffAchievementEvent);
            }
        }
    }
}
