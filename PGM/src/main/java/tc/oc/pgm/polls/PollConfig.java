package tc.oc.pgm.polls;

import org.bukkit.configuration.ConfigurationSection;
import tc.oc.pgm.PGM;
import tc.oc.pgm.match.MatchManager;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

public class PollConfig {

    private final ConfigurationSection config;
    private final MatchManager matchManager;

    @Inject PollConfig(ConfigurationSection config, MatchManager matchManager) {
        this.config = checkNotNull(config.getConfigurationSection("poll"));
        this.matchManager = matchManager;
    }

    public Path getPollBlacklistPath() {
        Path pollPath = Paths.get(config.getString("maps.path", "default.txt"));
        if(!pollPath.isAbsolute()) {
            pollPath = matchManager.getPluginDataFolder().resolve(pollPath);
        }
        return pollPath;
    }

    public boolean enabled() {
        return config.getBoolean("enabled", true);
    }

}
