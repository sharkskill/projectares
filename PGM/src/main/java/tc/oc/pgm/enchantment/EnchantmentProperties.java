package tc.oc.pgm.enchantment;

import org.bukkit.enchantments.Enchantment;

import javax.inject.Inject;
import java.util.Set;

public class EnchantmentProperties {
    private final Set<Enchantment> disabledEnchantments;

    @Inject EnchantmentProperties(Set<Enchantment> disabledEnchantments) {
        this.disabledEnchantments = disabledEnchantments;
    }

    boolean shouldBlock(Enchantment enchantment) {
        return disabledEnchantments.contains(enchantment);
    }
}
