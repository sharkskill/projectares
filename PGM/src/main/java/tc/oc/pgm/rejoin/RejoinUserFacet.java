package tc.oc.pgm.rejoin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import tc.oc.commons.bukkit.event.targeted.TargetedEventHandler;
import tc.oc.commons.bukkit.inventory.Slot;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.events.MatchPlayerAddEvent;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchPlayerFinder;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.MatchUserFacet;
import tc.oc.pgm.match.inject.ForMatchUser;
import tc.oc.pgm.teams.TeamMatchModule;

import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RejoinUserFacet implements MatchUserFacet, Listener {

    private final Duration OFFLINE_CHECK_TIME = Duration.ofSeconds(1);

    private final UUID player;
    private final MatchPlayerFinder matchPlayerFinder;
    private final Optional<RejoinRules> rules;
    private final Map<Slot, ItemStack> inventory = new HashMap<>();
    private double health;
    private int exp;
    private float saturation;
    private int foodLevel;
    private Collection<PotionEffect> effects;

    private Competitor latestParticipatingTeam;
    private Location latestParticipatingLocation;
    private boolean allowedToRejoin = false;
    private Task offlineTask;
    private Duration offlineTime = Duration.ZERO;
    private int rejoins = 0;

    @Inject
    RejoinUserFacet(@ForMatchUser UUID player, MatchPlayerFinder matchPlayerFinder, Optional<RejoinRules> rules) {
        this.player = player;
        this.matchPlayerFinder = matchPlayerFinder;
        this.rules = rules;
    }

    @SuppressWarnings("deprecation")
    @TargetedEventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void processPlayerPartyChange(PlayerChangePartyEvent event) {
        if (!event.getPlayer().getUniqueId().equals(player)) return;
        if (event.getNewParty() == null) return;
        if (!rules.isPresent()) return;

        this.allowedToRejoin = event.isParticipating();
    }

    @SuppressWarnings("deprecation")
    @TargetedEventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void processPlayerQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().getUniqueId().equals(player)) return;
        MatchPlayer matchPlayer = matchPlayerFinder.getPlayer(event.getPlayer());
        if (matchPlayer == null) return;
        if (!rules.isPresent()) return;
        if (!matchPlayer.isParticipating()) return;
        if (!matchPlayer.getMatch().hasStarted() || matchPlayer.getMatch().isFinished()) return;

        rejoins++;

        final Map<Slot, ItemStack> carrying = new HashMap<>();
        Slot.Player.player().forEach(slot -> slot.item(event.getPlayer())
                .ifPresent(item -> carrying.put(slot, item)));
        inventory.clear();
        inventory.putAll(carrying);

        health = event.getPlayer().getHealth();

        exp = event.getPlayer().getTotalExperience();

        saturation = event.getPlayer().getSaturation();

        foodLevel = event.getPlayer().getFoodLevel();

        effects = new ArrayList<>();
        effects.addAll(event.getPlayer().getActivePotionEffects());

        if (allowedToRejoin && rejoins >= rules.get().maxRejoins) {
            blockPlayerFromRejoining(matchPlayer.getMatch(), event.getPlayer().getDisplayName(), true);
            return;
        }

        this.latestParticipatingTeam = matchPlayer.getCompetitor();
        this.latestParticipatingLocation = matchPlayer.getLocation();
        final Match match = matchPlayer.getMatch();
        final String displayName = event.getPlayer().getDisplayName();

        offlineTask = matchPlayer.getMatch().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ZERO, OFFLINE_CHECK_TIME, new Runnable() {
            @Override
            public void run() {
                addOfflineTime(match, displayName, inventory);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void processPlayerJoin(MatchPlayerAddEvent event) {
        if (!event.getPlayer().getUniqueId().equals(player)) return;

        MatchPlayer matchPlayer = event.getPlayer();
        if (matchPlayer == null) return;
        if (!rules.isPresent()) return;
        if (!matchPlayer.getMatch().hasStarted()) return;

        if (!allowedToRejoin) return;

        offlineTask.cancel();

        final TeamMatchModule tmm = matchPlayer.getMatch().getMatchModule(TeamMatchModule.class);
        if (tmm != null) {
            tmm.forceJoin(matchPlayer, latestParticipatingTeam);
            offlineTask = matchPlayer.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofMillis(500), new Runnable() {
                @Override
                public void run() {
                    matchPlayer.getBukkit().getInventory().clear();
                    restoreKeptInventory(event.getPlayer().getBukkit());
                    matchPlayer.getBukkit().teleport(latestParticipatingLocation);
                    matchPlayer.getBukkit().setHealth(health);
                    matchPlayer.getBukkit().setTotalExperience(exp);
                    matchPlayer.getBukkit().setSaturation(saturation);
                    matchPlayer.getBukkit().setFoodLevel(foodLevel);
                    matchPlayer.getBukkit().addPotionEffects(effects);
                }
            });
        }
    }

    private void blockPlayerFromRejoining(Match match, String displayName, boolean diedDueToRejoins) {
        this.allowedToRejoin = false;
        this.rejoins = 0;
        this.offlineTime = Duration.ZERO;
        this.offlineTask.cancel();

        latestParticipatingLocation.getBlock().setType(Material.CHEST);

        latestParticipatingLocation = latestParticipatingLocation.add(0, 0, -1);
        latestParticipatingLocation.getBlock().setType(Material.CHEST);


        Chest chest = (Chest) latestParticipatingLocation.getBlock().getState();
        inventory.forEach((slot, drop) -> chest.getBlockInventory().addItem(drop));
        inventory.clear();

        match.sendMessage(
                new Component(ChatColor.RED)
                        .extra(displayName + " ")
                        .extra(new TranslatableComponent("broadcast.rejoin.message"), ChatColor.RED)
                        .extra(" ")
                        .extra(new TranslatableComponent("broadcast.rejoin." + (diedDueToRejoins ? "rejoins" : "offlineTime")), ChatColor.RED));
    }

    private void addOfflineTime(Match match, String displayName, Map<Slot, ItemStack> inventory) {
        this.offlineTime = this.offlineTime.plus(OFFLINE_CHECK_TIME);

        if (allowedToRejoin && Comparables.greaterThan(offlineTime, rules.get().maxOfflineTime)) {
            this.inventory.clear();
            this.inventory.putAll(inventory);
            blockPlayerFromRejoining(match, displayName, false);
        }
    }

    private void restoreKeptInventory(Player bukkit) {
        final List<ItemStack> displaced = new ArrayList<>();
        final PlayerInventory inv = bukkit.getInventory();

        inventory.forEach((slot, keptStack) -> {
            final ItemStack invStack = slot.getItem(bukkit);

            if (invStack == null || slot instanceof Slot.Armor) {
                slot.putItem(inv, keptStack);
            } else {
                if (invStack.isSimilar(keptStack)) {
                    int n = Math.min(keptStack.getAmount(), invStack.getMaxStackSize() - invStack.getAmount());
                    invStack.setAmount(invStack.getAmount() + n);
                    keptStack.setAmount(keptStack.getAmount() - n);
                }
                if (keptStack.getAmount() > 0) {
                    displaced.add(keptStack);
                }
            }

            for (ItemStack stack : displaced) {
                inv.addItem(stack);
            }
        });
        inventory.clear();
    }

    @Override
    public void disable() {
        if (offlineTask != null) {
            offlineTask.cancel();
        }
    }
}
