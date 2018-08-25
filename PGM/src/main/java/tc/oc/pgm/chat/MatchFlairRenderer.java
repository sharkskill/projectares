package tc.oc.pgm.chat;

import net.md_5.bungee.api.ChatColor;
import tc.oc.api.bukkit.users.BukkitUserStore;
import tc.oc.api.docs.virtual.UserDoc;
import tc.oc.api.minecraft.MinecraftService;
import tc.oc.commons.bukkit.chat.NameFlag;
import tc.oc.commons.bukkit.chat.NameType;
import tc.oc.commons.bukkit.flairs.FlairConfiguration;
import tc.oc.commons.bukkit.flairs.FlairRenderer;
import tc.oc.commons.bukkit.nick.Identity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

/**
 * Add mapmaker flair and hide flairs not shown while participating
 */
@Singleton
public class MatchFlairRenderer extends FlairRenderer {

    private static final String MAPMAKER_FLAIR_LEGACY = ChatColor.BLUE + "*";

    private final MatchFlairCache cache;

    @Inject MatchFlairRenderer(MinecraftService minecraftService, BukkitUserStore userStore, FlairConfiguration flairConfiguration, MatchFlairCache cache) {
        super(minecraftService, userStore, flairConfiguration);
        this.cache = cache;
    }

    @Override
    public String getLegacyName(Identity identity, NameType type) {
        String name;
        if (!(type.style.contains(NameFlag.FLAIR) && type.reveal)) {
            name = "";
        }
        else if (identity.isConsole()) {
            name = ChatColor.GOLD + "❖";
        }
        else {
            boolean playing = this.cache.isParticipating(identity.getPlayerId());

            Stream<UserDoc.Flair> flairs = getFlairs(identity);
            if (playing) {
                flairs = flairs.filter(f -> f.visible_while_participating);
            }
            name = flairs.map(flair -> flair.text).reduce("", String::concat);
        }

        if(!type.style.contains(NameFlag.MAPMAKER)) return name;

        if (this.cache.isMapMaker(identity.getPlayerId())) {
            name = MAPMAKER_FLAIR_LEGACY + name;
        }

        return name;
    }
}
