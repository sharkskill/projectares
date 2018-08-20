package tc.oc.pgm.mutation.types;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.api.util.Permissions;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.modules.InfoModule;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.MutationMatchModule;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A mutation module that only works on UHC matches
 */
public interface UHCMutation extends MutationModule {

    default Object[] componentObjects() {
        return new Object[0];
    }

    default void broadcast() {
        BaseComponent broadcast = new Component(ChatColor.GRAY, ChatColor.BOLD)
                .extra("[")
                .extra(new Component(new TranslatableComponent("mutation.scenario"), ChatColor.AQUA))
                .extra("] ")
                .extra("[")
                .extra(new Component(new TranslatableComponent(mutation().getName(), componentObjects()), ChatColor.AQUA))
                .extra("] ")
                .extra(new Component(new TranslatableComponent(mutation().getBroadcast(), componentObjects()), ChatColor.YELLOW).bold(false));
        match().sendMessage(broadcast);
    }

    default ItemStack[] items() {
        return null;
    }

    default void apply(Player player) {
        if (items() != null) {
            player.getInventory().addItem(items());
        }
    }

    default boolean isUHC() {
        return match().getMap().getContext().needModule(InfoModule.class).getGamemodes().contains(MapDoc.Gamemode.uhc);
    }

    default void damage(Player player, double newHealth) {
        player.damage(0);
        double difference = player.getHealth() - newHealth;

        if (player.getAbsorption() > 0) {
            if (player.getAbsorption() < difference) {
                player.setAbsorption(0);
                player.setHealth(difference - player.getAbsorption());
                return;
            } else {
                player.setAbsorption(player.getAbsorption() - (float)difference);
                return;
            }
        }
        player.setHealth(newHealth);
    }

    default BaseComponent message(String message) {
        return message(message, ChatColor.RED);
    }

    default BaseComponent message(String message, ChatColor color, Object... arguments) {
        BaseComponent broadcast = new Component(ChatColor.GRAY, ChatColor.BOLD)
                .extra("[")
                .extra(new Component(new TranslatableComponent("mutation.scenario"), ChatColor.AQUA))
                .extra("] ")
                .extra("[")
                .extra(new Component(new TranslatableComponent(mutation().getName()), ChatColor.AQUA))
                .extra("] ")
                .extra(new Component(new TranslatableComponent(message, arguments), color).bold(false));
        return broadcast;
    }

    abstract class Impl extends MutationModule.Impl implements UHCMutation {

        private Set<UUID> applied = new HashSet<>();

        public Impl(final Match match, final Mutation mutation) {
            super(match, mutation);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(PlayerChangePartyEvent event) {
            if (!applied.contains(event.getPlayer().getUniqueId()) && items() != null) {
                match().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(1), () -> {
                    if (event.getNewParty() != null && event.getNewParty().isParticipating()) {
                        apply(event.getPlayer().getBukkit());
                        applied.add(event.getPlayer().getUniqueId());
                    }
                });
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(MatchBeginEvent event) {
            MutationMatchModule mmm = match().module(MutationMatchModule.class).get();
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(mmm.broadcastTime(), this::broadcast);
            mmm.broadcastTime(mmm.broadcastTime().plus(Duration.ofSeconds(10)));

            for (MatchPlayer player : event.getMatch().getParticipatingPlayers()) {
                if (!applied.contains(player.getUniqueId()) && items() != null) {
                    match().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(1), () -> {
                        apply(player.getBukkit());
                        applied.add(player.getUniqueId());
                    });
                }
                if (!player.getBukkit().isWhitelisted()) {
                    player.getBukkit().setWhitelisted(true);
                    player.sendMessage(ChatColor.YELLOW + "Whitelisted.");
                }
            }


            for (Item item : event.getWorld().getEntitiesByClass(Item.class)) {
                item.remove();
            }

            match().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofMinutes(1), () -> {
                for (StorageMinecart minecart : event.getWorld().getEntitiesByClass(StorageMinecart.class)) {
    //                List<ItemStack> items = minecart.getInventory().storage();
    //                minecart.getLocation().getBlock().setType(Material.CHEST);
    //                Bukkit.broadcastMessage(minecart.getLocation().getBlock().getType().name());
    //                for (ItemStack item : items) {
    //                    if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
    //                        continue;
    //                    }
    //                    ((Chest)minecart.getLocation().getBlock().getState()).getBlockInventory().addItem(item);
    //                }
                    minecart.getInventory().clear();
                    minecart.remove();
                }
            });

        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(MatchPlayerDeathEvent event) {
            if (event.getMatch().getParticipatingPlayers().size() > 10 && !event.getVictim().getBukkit().hasPermission(Permissions.STAFF)) {
                event.getVictim().getBukkit().setWhitelisted(false);
                event.getVictim().sendMessage(ChatColor.YELLOW + "Kicking in 30 seconds");
                event.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(30), () -> event.getVictim().getBukkit().kickPlayer("Thanks for playing"));
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(ChunkLoadEvent event) {

            for (StorageMinecart minecart : Arrays.stream(event.getChunk().getEntities()).filter(entity -> entity instanceof StorageMinecart).toArray(StorageMinecart[]::new)) {
//                List<ItemStack> items = minecart.getInventory().storage();
//                minecart.getLocation().getBlock().setType(Material.CHEST);
//                Bukkit.broadcastMessage(minecart.getLocation().getBlock().getType().name());
//                for (ItemStack item : items) {
//                    if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
//                        Bukkit.broadcastMessage("null");
//                        continue;
//                    }
//                    Bukkit.broadcastMessage(item.toString());
//                    ((Chest)minecart.getLocation().getBlock().getState()).getBlockInventory().addItem(item);
//                }
                minecart.getInventory().clear();
                minecart.remove();
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(ChunkPopulateEvent event) {

            for (StorageMinecart minecart : Arrays.stream(event.getChunk().getEntities()).filter(entity -> entity instanceof StorageMinecart).toArray(StorageMinecart[]::new)) {
//                List<ItemStack> items = minecart.getInventory().storage();
//                minecart.getLocation().getBlock().setType(Material.CHEST);
//                Bukkit.broadcastMessage(minecart.getLocation().getBlock().getType().name());
//                for (ItemStack item : items) {
//                    if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
//                        Bukkit.broadcastMessage("null");
//                        continue;
//                    }
//                    Bukkit.broadcastMessage(item.toString());
//                    ((Chest)minecart.getLocation().getBlock().getState()).getBlockInventory().addItem(item);
//                }
                minecart.getInventory().clear();
                minecart.remove();
            }

            for (Item item : Arrays.stream(event.getChunk().getEntities()).filter(entity -> entity instanceof Item).toArray(Item[]::new)) {
                item.remove();
            }
        }

        @Override
        public void enable() {
            if (isUHC()) {
                super.enable();
            }
        }

        @Override
        public void disable() {
            if (isUHC()) {
                super.disable();
            }
        }
    }

}
