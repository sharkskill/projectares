package tc.oc.pgm.achievements.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.achievements.event.OneOffAchievementEvent;
import tc.oc.pgm.achievements.event.StatAchievementCompleteEvent;
import tc.oc.pgm.achievements.type.OneOffAchievement;
import tc.oc.pgm.achievements.type.StatAchievement;

public class OneOffAchievementCompleteListener implements Listener {

    @EventHandler
    public void onOneOffAchievementComplete(OneOffAchievementEvent event) {
        Player player = event.getPlayer();
        OneOffAchievement oneOffAchievement = event.getOneOffAchievement();

        player.sendMessage(ChatColor.STRIKETHROUGH.toString() + "------------------------------------");
        player.sendMessage(ChatColor.BOLD + "Achievement Unlocked" + ChatColor.GRAY + " \u00BB " + ChatColor.BLUE + oneOffAchievement.getUnlockMessage());
        player.sendMessage(ChatColor.STRIKETHROUGH.toString() + "------------------------------------");

        if (event.isSpecialAchievement()) Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + " has " + ChatColor.AQUA + oneOffAchievement.getUnlockMessage());
    }
}
