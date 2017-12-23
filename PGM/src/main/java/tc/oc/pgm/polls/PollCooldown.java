package tc.oc.pgm.polls;

import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.commons.core.plugin.PluginFacet;
import tc.oc.pgm.Config;
import tc.oc.pgm.events.CycleEvent;

@Singleton
public class PollCooldown implements PluginFacet, Listener {

    private int mutationCooldown;
    private int mapCooldown;
    private boolean mutationPolled;

    public PollCooldown() {
        mutationCooldown = 0;
        mapCooldown = 0;
        mutationPolled = false;
    }

    @EventHandler()
    public void onMatchCycle(CycleEvent cycleEvent) {
        if (mutationCooldown > 0) {
            mutationCooldown--;
        }
        if (mapCooldown > 0) {
            mapCooldown--;
        }
        if (mutationPolled) {
            mutationCooldown = Config.Poll.mutationCooldown();
            mutationPolled = false;
        }
    }

    @EventHandler()
    public void onPollEnd(PollEndEvent pollEndEvent) {
        if (!pollEndEvent.getPoll().isSuccessful()) return;

        if (pollEndEvent.poll instanceof PollNextMap) {
            mapCooldown = Config.Poll.mapCooldown() + 1;
        } else if (pollEndEvent.poll instanceof PollMutation) {
            mutationPolled = true;
        }
    }

    public int mapCooldown() {
        return mapCooldown;
    }

    public int mutationCooldown() {
        return mutationCooldown;
    }
}
