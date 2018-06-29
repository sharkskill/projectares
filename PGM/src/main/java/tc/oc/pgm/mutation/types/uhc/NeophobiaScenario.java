package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.HashSet;
import java.util.Set;

public class NeophobiaScenario extends UHCMutation.Impl {
    public NeophobiaScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    private Set<Material> craftedItems = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        if (event.getInventory().getResult() == null) {
            return;
        }

        Material type = event.getInventory().getResult().getType();

        if (!craftedItems.contains(type)) {
            craftedItems.add(type);

            event.getActor().damage(0);
            event.getActor().setHealth(event.getActor().getHealth() - 1);
        }
    }

    @Override
    public void disable() {
        craftedItems.clear();
        super.disable();
    }

}
