package tc.oc.pgm.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

@ListenerScope(MatchScope.RUNNING)
public class EnchantmentMatchModule extends MatchModule implements Listener {
    private final EnchantmentProperties properties;

    @Inject EnchantmentMatchModule(EnchantmentProperties properties) {
        this.properties = properties;
    }

    public EnchantmentProperties getProperties() {
        return properties;
    }

    @EventHandler(ignoreCancelled = true)
    public void disallowEnchant(EnchantItemEvent event) {
        event.getEnchantsToAdd().keySet().removeIf(properties::shouldBlock);
    }
}
