package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Location;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.TargetMutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SkyHighScenario extends UHCMutation.Impl implements TargetMutation {

    final private static Duration FREQUENCY = Duration.ofSeconds(30);
    final private static Duration START_TIME = Duration.ofMinutes(45);
    final private static int MAX_HEIGHT = 100;

    Instant next;

    public SkyHighScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public Object[] componentObjects() {
        Object[] list = new Object[2];
        list[0] = PeriodFormats.formatColonsLong(START_TIME).toPlainText();
        list[1] = MAX_HEIGHT;
        return list;
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public void target(List<MatchPlayer> players) {
        if (Comparables.lessThan(match().runningTime(), START_TIME)) {
            return;
        }

        players.forEach(player -> {
            Location location = player.getLocation();
            if (location.getY() < MAX_HEIGHT) {
                damage(player.getBukkit(), player.getBukkit().getHealth() - 1);
            }
        });

        next(match().getInstantNow().plus(frequency()));
    }

    @Override
    public int targets() {
        return match().getParticipatingPlayers().size();
    }

    @Override
    public Instant next() {
        return next;
    }

    @Override
    public void next(Instant time) {
        next = time;
    }

    @Override
    public Duration frequency() {
        return FREQUENCY;
    }

    @Override
    public void enable() {
        super.enable();
        TargetMutation.super.enable();
    }

    @Override
    public void tick() {
        TargetMutation.super.tick();
    }

}
