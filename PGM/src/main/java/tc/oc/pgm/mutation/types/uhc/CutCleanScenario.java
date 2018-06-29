package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.HashMap;
import java.util.Map;

public class CutCleanScenario extends UHCMutation.Impl {

    private Map<Material, Material> replacements;

    public CutCleanScenario(Match match, Mutation mutation) {
        super(match, mutation);
        fillReplacements();
    }

    private void fillReplacements() {
        replacements = new HashMap<>();
        replacements.put(Material.RAW_BEEF, Material.COOKED_BEEF);
        replacements.put(Material.RAW_CHICKEN, Material.COOKED_CHICKEN);
        replacements.put(Material.RAW_FISH, Material.COOKED_FISH);
        replacements.put(Material.MUTTON, Material.COOKED_MUTTON);
        replacements.put(Material.RABBIT, Material.COOKED_RABBIT);
        replacements.put(Material.IRON_ORE, Material.IRON_INGOT);
        replacements.put(Material.GOLD_ORE, Material.GOLD_INGOT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        if (replacements.containsKey(stack.getType())) {
            stack.setType(replacements.get(stack.getType()));
        }
    }

    @Override
    public void disable() {
        super.disable();
    }

}
