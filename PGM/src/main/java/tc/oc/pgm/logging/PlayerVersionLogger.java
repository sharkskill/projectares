package tc.oc.pgm.logging;

import org.apache.commons.io.FileUtils;
import tc.oc.api.bukkit.users.BukkitUserStore;
import tc.oc.minecraft.protocol.MinecraftVersion;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerVersionLogger {

    private final BukkitUserStore userStore;

    private File playerVersionLogFile;

    @Inject PlayerVersionLogger(BukkitUserStore userStore) {
        this.userStore = userStore;

        playerVersionLogFile = new File("player_versions.log");
    }


    public void logPlayerVersions() {
        Map<String, Integer> playerCountVersionMap = new HashMap<>();
        userStore.stream().forEach(player -> {
            int version = player.getProtocolVersion();
            playerCountVersionMap.put(MinecraftVersion.describeProtocolSimplified(version), playerCountVersionMap.getOrDefault(version, 0) + 1);
        });

        StringBuilder builder = new StringBuilder();
        builder.append("[\'");
        builder.append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        builder.append("\'");
        for (int i = 7; i <= 12; i++) {
            builder.append(", \'");
            builder.append(playerCountVersionMap.getOrDefault("1." + Integer.toString(i), 0));
            builder.append("\'");
        }
        builder.append("]\n");

        try {
            FileUtils.writeStringToFile(playerVersionLogFile, builder.toString(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
