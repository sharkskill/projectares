package tc.oc.pgm.achievements;


import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import tc.oc.pgm.achievements.type.stat.KillAchievements;
import tc.oc.pgm.achievements.type.stat.ObjectiveAchievements;

import java.util.LinkedHashSet;
import java.util.Set;

public class AchievementManager {
    private final Plugin parent;
    private final PluginManager pm;
    private Set<Achievement> achievements = new LinkedHashSet<>();

    private KillAchievements killAchievements;
    private ObjectiveAchievements objectiveAchievements;

    public AchievementManager(Plugin parent) {
        this.parent = parent;
        this.pm = parent.getServer().getPluginManager();
        this.registerAchievement(this.killAchievements = new KillAchievements());
        this.registerAchievement(this.objectiveAchievements = new ObjectiveAchievements());
    }

    private void registerAchievement(Achievement achievement) {
        this.achievements.add(achievement);

        if (achievement instanceof Listener) pm.registerEvents((Listener) achievement, parent);
    }

    public Set getAchievements() {
        return achievements;
    }

    public KillAchievements getKillAchievements() {
        return this.killAchievements;
    }

    public ObjectiveAchievements getObjectiveAchievements() {
        return this.objectiveAchievements;
    }

}
