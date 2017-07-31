package tc.oc.pgm.menu.gui;

import com.google.api.client.util.Lists;
import org.bukkit.entity.Player;
import tc.oc.commons.bukkit.gui.buttons.Button;
import tc.oc.commons.bukkit.gui.interfaces.SinglePageInterface;
import tc.oc.pgm.achievements.Achievement;
import tc.oc.pgm.achievements.AchievementManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AchievementsInterface extends SinglePageInterface {

    @Inject AchievementManager achievementManager;

    public AchievementsInterface(Player player) {
        super(player, new ArrayList<>(), 54, "Achievements", MainMenuInterface.getInstance());
        update();
    }

    @Override
    public void setButtons() {
        List<Button> buttons = new ArrayList<>();
        List<Achievement> achievements = Lists.newArrayList(achievementManager.getAchievements());
        Collections.sort(achievements, new Comparator<Achievement>() {
            public int compare(Achievement a1, Achievement a2) {
                return a1.getName().compareTo(a2.getName());
            }
        });


    }


}
