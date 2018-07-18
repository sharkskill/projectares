package tc.oc.pgm.mutation.types;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tc.oc.api.docs.virtual.MapDoc;

import tc.oc.api.util.Permissions;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.util.TimeUtils;
import tc.oc.pgm.PGM;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.Repeatable;
import tc.oc.pgm.modules.InfoModule;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.MutationMatchModule;
import tc.oc.time.Time;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    default boolean isUHC() {
        return match().getMap().getContext().needModule(InfoModule.class).getGamemodes().contains(MapDoc.Gamemode.uhc);
    }

    default void damage(Player player, double newHealth) {
        player.damage(0);
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

        public Impl(final Match match, final Mutation mutation) {
            super(match, mutation);;
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(MatchBeginEvent event) {
            MutationMatchModule mmm = match().module(MutationMatchModule.class).get();
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(mmm.broadcastTime(), this::broadcast);
            mmm.broadcastTime(mmm.broadcastTime().plus(Duration.ofSeconds(10)));

            for (MatchPlayer player : event.getMatch().getParticipatingPlayers()) {
                player.getBukkit().setWhitelisted(true);
                player.sendMessage(ChatColor.YELLOW + "Whitelisted.");
            }

//            for (StorageMinecart minecart : event.getMatch().getWorld().getEntitiesByClass(StorageMinecart.class)) {
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
//                minecart.getInventory().clear();
//                minecart.remove();
//            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(MatchPlayerDeathEvent event) {
            if (event.getMatch().getParticipatingPlayers().size() > 10 && event.getVictim().getBukkit().hasPermission(Permissions.STAFF)) {
                event.getVictim().getBukkit().setWhitelisted(false);
                event.getVictim().sendMessage(ChatColor.YELLOW + "Kicking in 30 seconds");
                event.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(30), () -> event.getVictim().getBukkit().kickPlayer("Thanks for playing"));
            }

        }

//        @EventHandler(priority = EventPriority.HIGHEST)
//        public void onMatchStart(ChunkLoadEvent event) {
//            for (StorageMinecart minecart : event.getWorld().getEntitiesByClass(StorageMinecart.class)) {
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
//                minecart.getInventory().clear();
//                minecart.remove();
//            }
//        }

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
