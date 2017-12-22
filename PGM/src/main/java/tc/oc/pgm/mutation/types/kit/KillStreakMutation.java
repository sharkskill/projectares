package tc.oc.pgm.mutation.types.kit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.pgm.filters.matcher.player.KillStreakFilter;
import tc.oc.pgm.killreward.KillReward;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.kits.PotionKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

import java.util.stream.Stream;

public class KillStreakMutation extends KitMutation {

    private static final KillReward[] killRewards = {
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(1), false, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(2), false, false), new PotionKit(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(3), false, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(4), false, false), new PotionKit(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(5), false, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(6), false, false), new PotionKit(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(7), false, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(8), false, false), new PotionKit(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(9), false, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.singleton(10), false, false), new PotionKit(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2))),
            new KillReward(ImmutableList.of(item(Material.AIR)), new KillStreakFilter(Range.atLeast(11), true, false), new FreeItemKit(item(Material.GOLDEN_APPLE))),
    };

    public KillStreakMutation(Match match, boolean force) {
        super(match, force);
        Stream.of(killRewards).forEach(rewards::add);
    }
}
