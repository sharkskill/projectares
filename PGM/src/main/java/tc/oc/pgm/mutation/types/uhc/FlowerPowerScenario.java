package tc.oc.pgm.mutation.types.uhc;

import com.google.common.collect.Range;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import tc.oc.pgm.events.BlockTransformEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowerPowerScenario extends UHCMutation.Impl {
    private List<Material> flowers;

    private static final List<Material> materials = Collections.unmodifiableList(Arrays.asList(Material.values()));
    static Range<Integer> SIZE;

    public FlowerPowerScenario(Match match, Mutation mutation) {
        super(match, mutation);
        SIZE = Range.openClosed(0, materials.size() - 1);
        fillFlowers();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (flowers.contains(event.getEntity().getItemStack().getType())) {
            Bukkit.broadcastMessage(materials.get(entropy().randomInt(SIZE)).name());
            event.getEntity().getItemStack().setType(materials.get(entropy().randomInt(SIZE)));
        }
    }

    private void fillFlowers() {
        flowers = new ArrayList<>();
        flowers.add(Material.YELLOW_FLOWER);
        flowers.add(Material.RED_ROSE);
    }

    @Override
    public void disable() {
        super.disable();
    }

}
