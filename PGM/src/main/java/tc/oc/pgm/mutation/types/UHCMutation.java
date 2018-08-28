package tc.oc.pgm.mutation.types;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.modules.InfoModule;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.MutationMatchModule;
import tc.oc.pgm.spawns.events.ParticipantSpawnEvent;

import java.time.Duration;
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
        damage(player, newHealth, true);
    }

    default void damage(Player player, double newHealth, boolean absorption) {
        player.damage(0);
        double difference = player.getHealth() - newHealth;

        if (absorption && player.getAbsorption() > 0) {
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
        public void onSpawn(ParticipantSpawnEvent event) {
            apply(event.getPlayer().getBukkit());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMatchStart(MatchBeginEvent event) {
            MutationMatchModule mmm = match().module(MutationMatchModule.class).get();
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(mmm.broadcastTime(), this::broadcast);
            mmm.broadcastTime(mmm.broadcastTime().plus(Duration.ofSeconds(10)));
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
