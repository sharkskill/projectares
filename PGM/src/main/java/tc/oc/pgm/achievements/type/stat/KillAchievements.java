package tc.oc.pgm.achievements.type.stat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.commons.bukkit.stats.StatsUtil;
import tc.oc.pgm.achievements.event.StatAchievementCompleteEvent;
import tc.oc.pgm.achievements.type.StatAchievement;
import tc.oc.pgm.events.MatchPlayerDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KillAchievements extends StatAchievement implements Listener {

    public static ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 10, 50, 100, 500, 1000, 2500, 5000, 10000, 25000, 50000, 75000, 100000));

    public KillAchievements() {
        super("Kills", ChatColor.AQUA + ChatColor.BOLD.toString() + "Kills", "Reach x amount of kills", numbers);
    }

    @EventHandler
    public void onPlayerDeath(MatchPlayerDeathEvent event) {
        if (event.getKiller() != null && event.getOnlineKiller() != null && event.isEnemyKill()) {
            Player player = event.getOnlineKiller().getBukkit();
            HashMap<String, Double> stats = StatsUtil.getStats(event.getOnlineKiller().getBukkit());
            int kills = stats.get("kills").intValue();
            if (numbers.contains(kills)) {
                StatAchievementCompleteEvent statAchievementCompleteEvent = new StatAchievementCompleteEvent(player, this, kills, kills >= 25000);
                Bukkit.getPluginManager().callEvent(statAchievementCompleteEvent);
            }
        }
    }
}
