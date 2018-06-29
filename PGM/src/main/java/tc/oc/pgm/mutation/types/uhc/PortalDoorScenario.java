package tc.oc.pgm.mutation.types.uhc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.commons.bukkit.util.BlockUtils;
import tc.oc.commons.bukkit.util.WorldBorderUtils;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.events.BlockTransformEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PortalDoorScenario extends UHCMutation.Impl {

    private final static int BANNED_RADIUS = 1;
    private final static int FLOWING_RADIUS = 3;
    private final static Duration DOOR_COOLDOWN = Duration.ofSeconds(15);

    private List<Material> doors;
    private List<Material> fallingBlocks;
    private List<Material> bannedBlocks;
    private List<Location> placedDoors;

    private Map<UUID, Duration> cooldowns;

    public PortalDoorScenario(Match match, Mutation mutation) {
        super(match, mutation);
        addDoors();
    }

    private void addDoors() {
        doors = new ArrayList<>();
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.IRON_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.SPRUCE_DOOR);
        doors.add(Material.WOOD_DOOR);
        doors.add(Material.WOODEN_DOOR);

        fallingBlocks = new ArrayList<>();
        fallingBlocks.add(Material.ANVIL);
        fallingBlocks.add(Material.SAND);
        fallingBlocks.add(Material.GRAVEL);
        fallingBlocks.add(Material.CONCRETE_POWDER);
        fallingBlocks.add(Material.DRAGON_EGG);

        bannedBlocks = new ArrayList<>();
        bannedBlocks.add(Material.LAVA);
        bannedBlocks.add(Material.STATIONARY_LAVA);
        bannedBlocks.add(Material.WATER);
        bannedBlocks.add(Material.STATIONARY_WATER);

        placedDoors = new ArrayList<>();

        cooldowns = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(BlockPlaceEvent event) {
        if (placedDoors.size() > 0) {
            for (int x = -BANNED_RADIUS; x <= BANNED_RADIUS; x++) {
                int y = 0;
                for (int z = -BANNED_RADIUS; z <= BANNED_RADIUS; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }
                    Location newLocation = event.getBlock().getLocation().add(x, y, z);
                    if (doors.contains(newLocation.getBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (doors.contains(event.getBlock().getType()) && !placedDoors.contains(event.getBlock().getLocation())) {
            Location location = event.getBlock().getLocation();

            Location below = location.clone().add(0, -1, 0);
            if (fallingBlocks.contains(below.getBlock().getType())) {
                return;
            }

            placedDoors.add(location);
            MatchPlayer player = match().getPlayer(event.getPlayer());
            if (player != null) {
                player.sendMessage(message("mutation.type.doorportal.created"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerBucketEmptyEvent event) {
        if (placedDoors.size() > 0) {
            for (int x = -FLOWING_RADIUS; x <= FLOWING_RADIUS; x++) {
                for (int y = -FLOWING_RADIUS; y <= FLOWING_RADIUS; y++) {
                    for (int z = -FLOWING_RADIUS; z <= FLOWING_RADIUS; z++) {
                        Location newLocation = event.getBlockClicked().getLocation().add(x, y, z);
                        if (doors.contains(newLocation.getBlock().getType())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(BlockFromToEvent event) {
        Material type = event.getBlock().getType();
        if (placedDoors.size() > 0 && bannedBlocks.contains(type)) {
            for (int x = -FLOWING_RADIUS; x <= FLOWING_RADIUS; x++) {
                for (int y = -FLOWING_RADIUS; y <= FLOWING_RADIUS; y++) {
                    for (int z = -FLOWING_RADIUS; z <= FLOWING_RADIUS; z++) {
                        Location newLocation = event.getToBlock().getLocation().add(x, y, z);
                        if (doors.contains(newLocation.getBlock().getType())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Location above = location.clone().add(0, 1, 0);

        if (doors.contains(above.getBlock().getType()) && !event.getBlock().getType().equals(Material.WOODEN_DOOR)) {
            event.setCancelled(true);
            return;
        }

        placedDoors.remove(event.getBlock().getLocation());
        placedDoors.remove(event.getBlock().getLocation().clone().add(0, 1, 0));
        placedDoors.remove(event.getBlock().getLocation().clone().subtract(0, 1, 0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (placedDoors.size() > 1 && inDoor(event.getTo()) && !inDoor(event.getFrom())) {
            if (!cooldowns.containsKey(uuid)) {
                cooldowns.put(uuid, match().runningTime());
                teleport(event.getActor(), event.getTo());
                return;
            }

            Duration cooldown = cooldowns.get(event.getPlayer().getUniqueId());
            Duration currentTime = match().runningTime();
            Duration difference = currentTime.minus(cooldown);
            if (Comparables.greaterOrEqual(difference, DOOR_COOLDOWN)) {
                cooldowns.put(uuid, match().runningTime());
                teleport(event.getActor(), event.getTo());
            } else {
                MatchPlayer player = match().getPlayer(event.getPlayer());
                if (player != null) {
                    player.sendMessage(message("mutation.type.doorportal.cooldown", ChatColor.RED, PeriodFormats.formatColons(DOOR_COOLDOWN.minus(difference))));
                }
            }
        }
    }

    private void teleport(Player player, Location to) {
        int tries = 100;
        while (tries > 0) {
            tries--;

            Random random = new Random();
            Location location = placedDoors.get(random.nextInt(placedDoors.size()));
            if (location.getBlockX() == to.getBlockX() &&
                    location.getBlockY() == to.getBlockY() &&
                    location.getBlockZ() == to.getBlockZ()) {
                continue;
            }
            if (!WorldBorderUtils.isInsideBorder(to)) {
                continue;
            }

            player.teleport(BlockUtils.base(location));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 3, 1));
            return;
        }
    }

    private boolean inDoor(Location location) {
        return placedDoors.stream().anyMatch(doorLocation -> location.getBlockX() == doorLocation.getBlockX() &&
                location.getBlockY() == doorLocation.getBlockY() &&
                location.getBlockZ() == doorLocation.getBlockZ());
    }

    @Override
    public void disable() {
        super.disable();
    }

}
