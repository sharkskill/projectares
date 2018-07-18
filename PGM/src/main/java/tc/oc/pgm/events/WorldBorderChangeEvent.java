package tc.oc.pgm.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderChangeEvent extends Event {

    public WorldBorderChangeEvent() {

    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
