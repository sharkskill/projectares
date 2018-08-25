package tc.oc.pgm.chat;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.api.docs.UserId;
import tc.oc.commons.core.plugin.PluginFacet;
import tc.oc.pgm.events.MatchLoadEvent;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.map.Contributor;
import tc.oc.pgm.match.Match;

import java.util.HashMap;
import java.util.List;

public class MatchFlairCache implements Listener, PluginFacet {
    private final HashMap<UserId, Boolean> participatingPlayers = Maps.newHashMap();
    private final List<UserId> mapMakers = Lists.newArrayList();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPartyChange(PlayerChangePartyEvent event) {
        participatingPlayers.put(event.getPlayer().getPlayerId(), event.isParticipating());
    }

    private void registerMakers(Match match) {
        mapMakers.clear();
        match.getMap().getInfo().authors.stream()
                .filter((c) -> c.getUser() != null)
                .map(Contributor::getUser)
                .forEach(mapMakers::add);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMatchLoad(MatchLoadEvent event) {
        registerMakers(event.getMatch());
    }

    public boolean isParticipating(UserId id) {
        return participatingPlayers.getOrDefault(id, false);
    }

    public boolean isMapMaker(UserId id) {
        return mapMakers.contains(id);
    }

    @Override
    public void disable() {
        mapMakers.clear();
        participatingPlayers.clear();
    }
}
