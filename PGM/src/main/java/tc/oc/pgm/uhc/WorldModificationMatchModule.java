package tc.oc.pgm.uhc;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import java.util.Arrays;

@ListenerScope(MatchScope.LOADED)
public class WorldModificationMatchModule extends MatchModule implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        handleChunk(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkPopulate(ChunkPopulateEvent event) {
        handleChunk(event);
    }

    private void handleChunk(ChunkEvent event) {
        Arrays.stream(event.getChunk().getEntities()).filter(e -> (e instanceof StorageMinecart || e instanceof Item)).forEach(Entity::remove);
    }
}
