package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class MoblessScenario extends UHCMutation.Impl {

    public MoblessScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(MatchBeginEvent event) {
        for (MatchPlayer player : event.getMatch().getParticipatingPlayers()) {
            player.getBukkit().getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 30));
            player.getBukkit().getInventory().addItem(new ItemStack(Material.FEATHER, 16));
            player.getBukkit().getInventory().addItem(new ItemStack(Material.LEATHER, 3));
            player.getBukkit().getInventory().addItem(new ItemStack(Material.STRING, 3));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void disable() {
        super.disable();
    }

}
