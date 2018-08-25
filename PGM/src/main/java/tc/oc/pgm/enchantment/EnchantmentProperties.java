package tc.oc.pgm.enchantment;

import org.bukkit.enchantments.Enchantment;

import java.util.Set;

class EnchantmentProperties {
    private final Set<Enchantment> disabledEnchantments;

    EnchantmentProperties(Set<Enchantment> disabledEnchantments) {
        this.disabledEnchantments = disabledEnchantments;
    }

    boolean shouldBlock(Enchantment enchantment) {
        return disabledEnchantments.contains(enchantment);
    }
}
