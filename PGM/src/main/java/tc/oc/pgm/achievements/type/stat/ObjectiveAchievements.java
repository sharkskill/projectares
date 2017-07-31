package tc.oc.pgm.achievements.type.stat;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tc.oc.commons.bukkit.stats.StatsUtil;
import tc.oc.pgm.achievements.event.StatAchievementCompleteEvent;
import tc.oc.pgm.achievements.type.StatAchievement;
import tc.oc.pgm.goals.Contribution;
import tc.oc.pgm.goals.events.GoalCompleteEvent;
import tc.oc.pgm.match.MatchPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectiveAchievements extends StatAchievement {

    public static ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 10, 25, 50, 100, 250, 500, 1000, 1500, 2000, 2500, 3750, 5000));

    public ObjectiveAchievements() {
        super("Objectives", ChatColor.AQUA + ChatColor.BOLD.toString() + "Objectives", "Reach x amount of objectives", numbers);
    }

    @EventHandler
    public void onObjectiveComplete(GoalCompleteEvent event) {
        ImmutableList<Contribution> contributions = event.getContributions();
        for (Contribution contribution : contributions) {
            MatchPlayer matchPlayer = contribution.getPlayerState().getMatchPlayer();
            if (matchPlayer != null) {
                Player player = matchPlayer.getBukkit();
                HashMap<String, Double> stats = StatsUtil.getStats(player);
                int objectives = stats.get("wool_placed").intValue() + stats.get("cores_leaked").intValue() + stats.get("destroyables_destroyed").intValue();
                if (numbers.contains(objectives)) {
                    StatAchievementCompleteEvent statAchievementCompleteEvent = new StatAchievementCompleteEvent(player, this, objectives, objectives >= 1000);
                    Bukkit.getPluginManager().callEvent(statAchievementCompleteEvent);
                }
            }
        }
    }
}
