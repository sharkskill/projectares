package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;

public class SuperToolsMutation extends ToolsMutation {

    final static FreeItemKit[] SUPER_TOOLS = new FreeItemKit[] {
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_PICKAXE)).enchant(Enchantment.DIG_SPEED, 5).name("Super Quick Pick").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_AXE)).enchant(Enchantment.DIG_SPEED, 5).name("Super Quick Axe").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_SPADE)).enchant(Enchantment.DIG_SPEED, 5).name("Super Quick Shovel").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.SHEARS)).enchant(Enchantment.DIG_SPEED, 5).name("Super Quick Shears").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.GLASS)).amount(32).get())
    };

    public SuperToolsMutation(Match match) {
        super(match, SUPER_TOOLS);
    }

}
