package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

public class OpMutation extends KitMutation {

    final static FreeItemKit OP = new FreeItemKit(new ItemBuilder(item(Material.GOLDEN_APPLE)).durability(1).get());

    public OpMutation(Match match) {
        super(match, true, OP);
    }
}
