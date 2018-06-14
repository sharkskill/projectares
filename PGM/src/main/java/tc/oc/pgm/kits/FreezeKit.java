package tc.oc.pgm.kits;

import tc.oc.commons.bukkit.freeze.FrozenPlayer;
import tc.oc.commons.bukkit.freeze.PlayerFreezer;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import java.time.Duration;

public class FreezeKit extends Kit.Impl {
    private final Duration duration;
    private final Duration before;
    private final Duration after;
    private final PlayerFreezer playerFreezer;

    public FreezeKit(Duration duration, Duration before, Duration after, PlayerFreezer playerFreezer) {
        this.duration = duration;
        this.before = before;
        this.after = after;
        this.playerFreezer = playerFreezer;
    }

    @Override
    public void apply(MatchPlayer player, boolean force, ItemKitApplicator items) {
        Match match = player.getMatch();
        if ((before != null && Comparables.greaterThan(match.runningTime(), before)) ||
                (after != null && Comparables.lessThan(match.runningTime(), after))) {
            return;
        }

        FrozenPlayer frozenPlayer = playerFreezer.freeze(player.getBukkit());
        match.getScheduler(MatchScope.RUNNING).createDelayedTask(this.duration, frozenPlayer::thaw);
    }
}
