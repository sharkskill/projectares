package tc.oc.pgm.achievements.type.oneoff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.pgm.achievements.event.OneOffAchievementEvent;
import tc.oc.pgm.achievements.event.StatAchievementCompleteEvent;
import tc.oc.pgm.achievements.type.OneOffAchievement;
import tc.oc.pgm.events.MatchEndEvent;
import tc.oc.pgm.match.MatchPlayer;

import java.time.Duration;

public class OneHourMatchAchievement extends OneOffAchievement implements Listener {

    public OneHourMatchAchievement() {
        super("OneHourMatch", ChatColor.AQUA + ChatColor.BOLD.toString() + "One Hour Match", "Play and finish in a match that lasts longer than 1 hour.", "Completed a 1 hour long match.");
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Duration duration = event.getMatch().getLength();
        if (duration.getSeconds() >= 3600 && duration.getSeconds() < 7200) {
            for (MatchPlayer matchPlayer : event.getMatch().getParticipatingPlayers()) {
                Player player = matchPlayer.getBukkit();
                OneOffAchievementEvent oneOffAchievementEvent = new OneOffAchievementEvent(player, this, false);
                Bukkit.getPluginManager().callEvent(oneOffAchievementEvent);
            }
        }
    }
}
