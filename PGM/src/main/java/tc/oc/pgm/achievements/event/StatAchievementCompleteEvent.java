package tc.oc.pgm.achievements.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tc.oc.pgm.achievements.type.StatAchievement;

public class StatAchievementCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final StatAchievement statAchievement;
    private final int statAmount;
    private boolean specialAchievement;

    public StatAchievementCompleteEvent(Player player, StatAchievement statAchievement, int statAmount, boolean specialAchievement) {
        this.player = player;
        this.statAchievement = statAchievement;
        this.statAmount = statAmount;
        this.specialAchievement = specialAchievement;
    }

    public Player getPlayer() {
        return player;
    }

    public StatAchievement getStatAchievement() {
        return statAchievement;
    }

    public int getStatAmount() {
        return statAmount;
    }

    public boolean isSpecialAchievement() {
        return specialAchievement;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
