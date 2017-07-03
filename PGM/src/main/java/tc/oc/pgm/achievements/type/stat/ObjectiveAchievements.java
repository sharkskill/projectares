package tc.oc.pgm.achievements.type.stat;

import org.bukkit.ChatColor;
import tc.oc.pgm.achievements.type.StatAchievement;

import java.util.ArrayList;
import java.util.Arrays;

public class ObjectiveAchievements extends StatAchievement {

    public static ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 10, 25, 50, 100, 250, 500, 1000, 1500, 2000, 2500, 3750, 5000));

    public ObjectiveAchievements() {
        super("Objectives", ChatColor.AQUA + ChatColor.BOLD.toString() + "Objectives", "Reach x amount of objectives", numbers);
        isSpecialAchievement = false;
    }
}
