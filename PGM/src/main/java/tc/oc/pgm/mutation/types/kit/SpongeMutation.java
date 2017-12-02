package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

public class SpongeMutation extends KitMutation {

    final static FreeItemKit[] WATER_STUFF = new FreeItemKit[] {
            new FreeItemKit(new ItemBuilder(item(Material.SPONGE)).amount(8).get()),
            new FreeItemKit(new ItemBuilder(item(Material.WATER_BUCKET)).amount(2).get())
    };

    public SpongeMutation(Match match) {
        super(match, true, WATER_STUFF);
    }
}
