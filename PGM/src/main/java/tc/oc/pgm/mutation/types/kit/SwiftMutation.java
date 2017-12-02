package tc.oc.pgm.mutation.types.kit;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.pgm.kits.PotionKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

public class SwiftMutation extends KitMutation {

    final static PotionKit[] SWIFT = new PotionKit[] {
            new PotionKit(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0)),
            new PotionKit(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0))
    };

    public SwiftMutation(Match match) {
        super(match, true, SWIFT);
    }
}
