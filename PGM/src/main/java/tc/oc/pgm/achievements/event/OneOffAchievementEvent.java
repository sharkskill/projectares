package tc.oc.pgm.achievements.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tc.oc.pgm.achievements.type.OneOffAchievement;
import tc.oc.pgm.achievements.type.StatAchievement;

public class OneOffAchievementEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final OneOffAchievement oneOffAchievement;
    private boolean specialAchievement;

    public OneOffAchievementEvent(Player player, OneOffAchievement oneOffAchievement, boolean specialAchievement) {
        this.player = player;
        this.oneOffAchievement = oneOffAchievement;
        this.specialAchievement = specialAchievement;
    }

    public Player getPlayer() {
        return player;
    }

    public OneOffAchievement getOneOffAchievement() {
        return oneOffAchievement;
    }


    public boolean isSpecialAchievement() {
        return specialAchievement;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
