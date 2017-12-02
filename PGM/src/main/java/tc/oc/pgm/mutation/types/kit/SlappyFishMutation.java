package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

public class SlappyFishMutation extends KitMutation {

    final static FreeItemKit FISH = new FreeItemKit(new ItemBuilder(item(Material.RAW_FISH)).enchant(Enchantment.KNOCKBACK, 5).name("Slappy Fish").get());

    public SlappyFishMutation(Match match) {
        super(match, true, FISH);
    }
}
