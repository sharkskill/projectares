package tc.oc.pgm.terrain;

import java.nio.file.Path;
import java.util.Random;

import org.bukkit.WorldCreator;
import tc.oc.commons.bukkit.util.NullChunkGenerator;

public class TerrainOptions implements WorldConfigurator {
    private final Path worldFolder;
    private final boolean vanilla;
    private final long seed;
    private final boolean initialPhysics;
    private final boolean keepChunksLoaded;
    private final int loadChunks;

    public TerrainOptions(Path worldFolder, boolean vanilla, Long seed, boolean initialPhysics, boolean keepChunksLoaded, int loadChunks) {
        this.worldFolder = worldFolder;
        this.vanilla = vanilla;
        this.seed = seed != null ? seed : new Random().nextLong();
        this.initialPhysics = initialPhysics;
        this.keepChunksLoaded = keepChunksLoaded;
        this.loadChunks = loadChunks;
    }

    public Path worldFolder() {
        return worldFolder;
    }

    boolean initialPhysics() {
        return initialPhysics;
    }

    public boolean keepChunksLoaded() {
        return keepChunksLoaded;
    }

    public int loadChunks() {
        return loadChunks;
    }

    @Override
    public void configureWorld(WorldCreator worldCreator) {
        worldCreator.generator(vanilla ? null : new NullChunkGenerator());
        worldCreator.seed(seed);
    }
}
