package tc.oc.pgm.kits;

import tc.oc.commons.bukkit.freeze.FrozenPlayer;
import tc.oc.commons.bukkit.freeze.PlayerFreezer;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import java.time.Duration;

public class FreezeKit extends Kit.Impl {
    private final Duration duration;
    private final PlayerFreezer playerFreezer;

    public FreezeKit(Duration duration, PlayerFreezer playerFreezer) {
        this.duration = duration;
        this.playerFreezer = playerFreezer;
    }

    @Override
    public void apply(MatchPlayer player, boolean force, ItemKitApplicator items) {
        FrozenPlayer frozenPlayer = playerFreezer.freeze(player.getBukkit());
        player.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(this.duration, () -> {
            frozenPlayer.thaw();
        });
    }
}
