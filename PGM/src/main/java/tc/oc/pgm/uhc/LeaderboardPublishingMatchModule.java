package tc.oc.pgm.uhc;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.api.docs.PlayerId;
import tc.oc.api.docs.UhcLeaderboardEntry;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.api.docs.virtual.UhcLeaderboardEntryDoc;
import tc.oc.api.leaderboard.UHCLeaderboardEntryService;
import tc.oc.commons.core.concurrent.Flexecutor;
import tc.oc.minecraft.scheduler.Sync;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.MatchEndEvent;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.events.ParticipantBlockTransformEvent;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.map.PGMMap;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.ParticipantState;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.stats.StatisticsConfiguration;
import tc.oc.pgm.victory.VictoryMatchModule;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ListenerScope(MatchScope.LOADED)
public class LeaderboardPublishingMatchModule extends MatchModule implements Listener {

    private final PGMMap map;
    private final Flexecutor executor;
    private final UHCLeaderboardEntryService service;
    private final UHCProperties properties;
    private final StatisticsConfiguration statsConfig;
    private final VictoryMatchModule victory;
    private final HashMap<PlayerId, AtomicLeaderboardEntryBase> entries = Maps.newHashMap();

    @Inject
    public LeaderboardPublishingMatchModule(PGMMap map, @Sync Flexecutor executor, UHCLeaderboardEntryService service, Optional<UHCProperties> properties, StatisticsConfiguration statsConfig, VictoryMatchModule victory) {
        this.map = map;
        this.executor = executor;
        this.service = service;
        this.properties = properties.orElse(null); // Never not null since not loaded w/o UHC
        this.statsConfig = statsConfig;
        this.victory = victory;
    }

    @Override
    public boolean shouldLoad() {
        return super.shouldLoad() && map.getDocument().gamemode().contains(MapDoc.Gamemode.uhc);
    }

    private void createEntry(PlayerId playerId) {
        executor.callback(service.findOrCreate(playerId), (e) -> {
            switch (properties.type) {
                case SOLO:
                    entries.put(playerId, new AtomicSoloLeaderboardEntry(e));
                    return;
                case TEAMS:
                    entries.put(playerId, new AtomicTeamLeaderboardEntry(e));
                    return;
                default:
                    throw new IllegalArgumentException("Don't know how to create leaderboard entry for type: " + properties.type.name());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoinLeave(PlayerChangePartyEvent event) {
        if (event.isJoiningMatch()) {
            // Do this on every re-join as well to keep data as current as possible.
            createEntry(event.getPlayer().getPlayerId());
        } else if (event.isLeavingMatch()) {
            // When they leave, save their entry to the DB and purge it from the cache.
            // No need to do this after the match ends, since everything is saved when the game ends.
            if (match.isRunning()) {
                AtomicLeaderboardEntryBase entry = entries.remove(event.getPlayer().getPlayerId());
                if (entry != null) {
                    service.update(entry);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(MatchPlayerDeathEvent event) {
        final ParticipantState killer = event.getKiller();

        if (killer == null || !statsConfig.deaths()) return;

        entries.get(killer.getPlayerId()).killsAtomic().incrementAndGet();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMine(ParticipantBlockTransformEvent event) {
        if (event.getPlayer() == null || !match.isRunning()) return;

        if (event.isBreak() && event.getBlock().getType() == Material.GOLD_ORE) {
            entries.get(event.getPlayer().getPlayerId()).goldAtomic().incrementAndGet();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnd(MatchEndEvent endEvent) {
        victory.leaders().stream().flatMap(Party::players).forEach(p -> entries.get(p.getPlayerId()).winsAtomic().incrementAndGet());
        service.updateMulti(entries.values());
        entries.clear();
    }

    abstract class AtomicLeaderboardEntryBase implements UhcLeaderboardEntryDoc.Base {
        private final UhcLeaderboardEntry base;
        private final AtomicInteger wins;
        private final AtomicInteger kills;
        private final AtomicInteger gold;

        public AtomicLeaderboardEntryBase(UhcLeaderboardEntry base, AtomicInteger wins, AtomicInteger kills, AtomicInteger gold) {
            this.base = base;
            this.wins = wins;
            this.kills = kills;
            this.gold = gold;
        }

        @Override
        public String _id() {
            return base._id();
        }

        AtomicInteger winsAtomic() {
            return wins;
        }

        AtomicInteger killsAtomic() {
            return kills;
        }

        AtomicInteger goldAtomic() {
            return gold;
        }
    }

    class AtomicSoloLeaderboardEntry extends AtomicLeaderboardEntryBase implements UhcLeaderboardEntryDoc.Solo {
        public AtomicSoloLeaderboardEntry(UhcLeaderboardEntry base) {
            super(base, new AtomicInteger(base.wins_solo()), new AtomicInteger(base.kills_solo()), new AtomicInteger(base.gold_solo()));
        }

        @Override
        public int wins_solo() {
            return winsAtomic().get();
        }

        @Override
        public int gold_solo() {
            return goldAtomic().get();
        }

        @Override
        public int kills_solo() {
            return killsAtomic().get();
        }
    }

    class AtomicTeamLeaderboardEntry extends AtomicLeaderboardEntryBase implements UhcLeaderboardEntryDoc.Teams {
        public AtomicTeamLeaderboardEntry(UhcLeaderboardEntry base) {
            super(base, new AtomicInteger(base.wins_teams()), new AtomicInteger(base.kills_teams()), new AtomicInteger(base.gold_teams()));
        }

        @Override
        public int wins_teams() {
            return winsAtomic().get();
        }

        @Override
        public int gold_teams() {
            return goldAtomic().get();
        }

        @Override
        public int kills_teams() {
            return killsAtomic().get();
        }
    }

}
