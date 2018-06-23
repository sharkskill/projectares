package tc.oc.pgm.terrain;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.MatchLoadEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.time.Duration;

/**
 * Freeze block physics after the match ends, and before it starts too,
 * unless pre-match-physics is enabled.
 */
@ListenerScope(MatchScope.LOADED)
public class ChunkLoaderListener implements Listener {

    private final TerrainOptions options;
    private final Match match;

    @Inject private ChunkLoaderListener(TerrainOptions options, Match match) {
        this.options = options;
        this.match = match;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(options.keepChunksLoaded() && Comparables.lessThan(this.match.runningTime(), Duration.ofMinutes(1))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMatchLoad(MatchLoadEvent event) {
        int loadChunks = options.loadChunks();
        if(loadChunks > 0) {
            match.getLogger().info("Chunks Loading: " + 2*(loadChunks/16)*(loadChunks/16));
            for (int x = -loadChunks/16; x <= loadChunks/16; x++) {
                for (int z = -loadChunks/16; z <= loadChunks/16; z++) {
                    event.getWorld().loadChunk(x, z);
                }
            }
        }
    }
}
