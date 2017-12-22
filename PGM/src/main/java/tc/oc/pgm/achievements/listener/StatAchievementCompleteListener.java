package tc.oc.pgm.achievements.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.achievements.event.StatAchievementCompleteEvent;
import tc.oc.pgm.achievements.type.StatAchievement;

public class StatAchievementCompleteListener implements Listener {

    @EventHandler
    public void onStatAchievementComplete(StatAchievementCompleteEvent event) {
        Player player = event.getPlayer();
        StatAchievement statAchievement = event.getStatAchievement();
        int statAmount = event.getStatAmount();

        player.sendMessage(ChatColor.STRIKETHROUGH.toString() + "------------------------------------");
        player.sendMessage(ChatColor.BOLD + "Achievement Unlocked" + ChatColor.GRAY + " \u00BB " + ChatColor.BLUE + "Reached " + statAmount + statAchievement.getDisplayName());
        player.sendMessage(ChatColor.STRIKETHROUGH.toString() + "------------------------------------");

        if (event.isSpecialAchievement()) Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + " has reached " + statAmount + statAchievement.getDisplayName());
    }
}
