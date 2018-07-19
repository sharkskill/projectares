package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class GoneFishingScenario extends UHCMutation.Impl {

    public GoneFishingScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public ItemStack[] items() {
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = rod.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 250, true);
        meta.addEnchant(Enchantment.LUCK, 250, true);
        rod.setItemMeta(meta);
        return new ItemStack[]{rod};
    }

    @Override
    public void disable() {
        super.disable();
    }

}
