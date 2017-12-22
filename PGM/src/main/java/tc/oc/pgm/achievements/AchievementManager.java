package tc.oc.pgm.achievements;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import tc.oc.pgm.PGM;
import tc.oc.pgm.achievements.listener.OneOffAchievementCompleteListener;
import tc.oc.pgm.achievements.listener.StatAchievementCompleteListener;
import tc.oc.pgm.achievements.type.oneoff.OneHourMatchAchievement;
import tc.oc.pgm.achievements.type.oneoff.TwoHourMatchAchievement;
import tc.oc.pgm.achievements.type.stat.KillAchievements;
import tc.oc.pgm.achievements.type.stat.ObjectiveAchievements;

import java.util.LinkedHashSet;
import java.util.Set;

public class AchievementManager {
    private final PGM pgm;
    private final PluginManager pm;
    private Set<Achievement> achievements = new LinkedHashSet<>();

    private KillAchievements killAchievements;
    private ObjectiveAchievements objectiveAchievements;

    private OneHourMatchAchievement oneHourMatchAchievement;
    private TwoHourMatchAchievement twoHourMatchAchievement;

    public AchievementManager(PGM pgm) {
        this.pgm = pgm;
        this.pm = pgm.getServer().getPluginManager();
        //Debug message
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Achievement manager loaded.");
        this.registerAchievement(this.killAchievements = new KillAchievements());
        this.registerAchievement(this.objectiveAchievements = new ObjectiveAchievements());
        this.registerAchievement(this.oneHourMatchAchievement = new OneHourMatchAchievement());
        this.registerAchievement(this.twoHourMatchAchievement = new TwoHourMatchAchievement());

        pm.registerEvents(new OneOffAchievementCompleteListener(), pgm);
        pm.registerEvents(new StatAchievementCompleteListener(), pgm);
    }

    private void registerAchievement(Achievement achievement) {
        this.achievements.add(achievement);

        if (achievement instanceof Listener) pm.registerEvents((Listener) achievement, pgm);
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

    public OneHourMatchAchievement hourMatchAchievement() {
        return this.oneHourMatchAchievement;
    }

    public TwoHourMatchAchievement twoHourMatchAchievement() {
        return this.twoHourMatchAchievement;
    }
}
