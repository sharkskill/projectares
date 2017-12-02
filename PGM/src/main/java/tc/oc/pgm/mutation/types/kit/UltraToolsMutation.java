package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;

public class UltraToolsMutation extends ToolsMutation {

    final static FreeItemKit[] ULTRA_TOOLS = new FreeItemKit[] {
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_PICKAXE)).enchant(Enchantment.DIG_SPEED, 10).name("Ultra Quick Pick").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_AXE)).enchant(Enchantment.DIG_SPEED, 10).name("Ultra Quick Axe").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.DIAMOND_SPADE)).enchant(Enchantment.DIG_SPEED, 10).name("Ultra uick Shovel").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.SHEARS)).enchant(Enchantment.DIG_SPEED, 10).name("Ultra Quick Shears").unbreakable(true).get()),
            new FreeItemKit(new ItemBuilder(item(Material.GLASS)).amount(64).get())
    };

    public UltraToolsMutation(Match match) {
        super(match, ULTRA_TOOLS);
    }
}
