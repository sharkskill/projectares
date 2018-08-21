package tc.oc.pgm.worldborder;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import java.time.Duration;

import org.bukkit.util.Vector;
import tc.oc.commons.bukkit.util.WorldBorderUtils;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.util.Comparables;
import tc.oc.commons.core.util.DefaultMapAdapter;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.WorldBorderChangeEvent;
import tc.oc.pgm.goals.events.GoalEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@ListenerScope(MatchScope.LOADED)
public class WorldBorderMatchModule extends MatchModule implements Listener {

    public List<WorldBorder> borders;
    private final Map<WorldBorder, Boolean> results = new DefaultMapAdapter<>(false);
    private @Nullable WorldBorder appliedBorder;
    private @Nullable Duration appliedAt;
    private boolean bedrock;
    private WorldBorder initial;
    private boolean appliedInitial = false;

    public WorldBorderMatchModule(Match match, List<WorldBorder> borders) {
        super(match);
        checkNotNull(borders);
        checkArgument(!borders.isEmpty());
        this.borders = borders;
    }

    @Override
    public void load() {
        super.load();

        WorldBorder initial = borders.get(0);

        if(initial != null) {
            logger.fine("Initializing with " + initial);
            apply(initial);
            this.initial = initial;
            appliedInitial = true;
        } else {
            reset();
        }
    }

    @Override
    public void enable() {
        super.enable();

        getMatch().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ZERO, Duration.ofSeconds(1), () -> {
            if(!update()) {
                refresh();
            }
        });
    }

    @Override
    public void disable() {
        freeze();

        super.disable();
    }

    @Nullable
    public WorldBorder getAppliedBorder() {
        return appliedBorder;
    }

    private void apply(WorldBorder border) {
        if (this.appliedInitial && this.initial == border) {
            return;
        }

        logger.fine("Applying " + border);
        Match match = getMatch();

        if (match.hasStarted()) {
            BaseComponent alert = new Component(ChatColor.GRAY, ChatColor.BOLD)
                    .extra("[")
                    .extra(new Component(new TranslatableComponent("prefixed.alert"), ChatColor.YELLOW))
                    .extra("] ")
                    .extra((new Component(new TranslatableComponent("match.worldborder.shrunk"), ChatColor.AQUA)).bold(false));
            match.sendMessage(alert);
        }


        org.bukkit.WorldBorder existingBorder = match.getWorld().getWorldBorder();
        double oldSize = appliedBorder != null ? appliedBorder.size : existingBorder.getSize();
        border.apply(match, existingBorder, appliedBorder != null, oldSize);
        appliedBorder = border;
        bedrock = appliedBorder.bedrock;
        appliedAt = getMatch().runningTime();

        match.callEvent(new WorldBorderChangeEvent());
    }

    private void reset() {
        logger.fine("Clearing border");

        appliedBorder = null;
        appliedAt = null;
        getMatch().getWorld().getWorldBorder().reset();
    }

    /**
     * Query the filters of all borders and apply them as needed.
     *
     * A border is applied when its filter goes from false to true, or when it becomes
     * active because of another border further down the list going from true to false.
     *
     * If multiple borders become active simultaneously, they are applied in order. This
     * allows a border to serve as the starting point for another moving border.
     */
    private boolean update() {
        WorldBorder lastMatched = null;
        boolean applied = false;

        for(WorldBorder border : borders) {
            if (border.broadcast && border.after != null) {
                Duration durationUntilShrink = border.after.minus(getMatch().runningTime()).plus(Duration.ofSeconds(1));
                if (!durationUntilShrink.isNegative() && isBroadcastTime(durationUntilShrink)) {
                    BaseComponent alert = new Component(new TranslatableComponent("match.worldborder.shrinking", PeriodFormats.formatColons(durationUntilShrink), border.getSize()/2), ChatColor.AQUA);
                    match.sendMessage(alert);
                }
            }

            if (border.after == null) {
                continue;
            }
            boolean newResult = Comparables.lessOrEqual(border.after, match.runningTime());
            boolean oldResult = results.put(border, newResult);
            if(newResult) lastMatched = border;

            if(!oldResult && newResult) {
                // On the filter's rising edge, apply the border
                applied = true;
                apply(border);
            } else if(oldResult && !newResult) {
                if(lastMatched == null) {
                    // On the filter's falling edge, apply the last border in the list with a passing filter
                    reset();
                } else {
                    // If no borders have passing filters, clear the border
                    apply(lastMatched);
                }
            }
        }

        return applied;
    }

    private boolean isBroadcastTime(Duration durationUntilShrink) {
        long minutes = durationUntilShrink.toMinutes();
        long seconds = durationUntilShrink.getSeconds() - (minutes * 60);

        return (minutes <= 5 &&
               (minutes == 5 && seconds == 0  ||
                minutes == 4 && seconds == 0  ||
                minutes == 3 && seconds == 0  ||
                minutes == 2 && seconds == 0  ||
                minutes == 1 && seconds == 0  ||
                minutes == 0 && seconds == 45 ||
                minutes == 0 && seconds == 30 ||
                minutes == 0 && seconds == 15 ||
                minutes == 0 && seconds == 10 ||
                minutes == 0 && seconds == 5  ||
                minutes == 0 && seconds == 4  ||
                minutes == 0 && seconds == 3  ||
                minutes == 0 && seconds == 2  ||
                minutes == 0 && seconds == 1));
    }

    /**
     * If the current border is moving, refresh its size/duration on all clients (to keep them in sync)
     */
    private void refresh() {
        if(appliedBorder != null) {
            appliedBorder.refresh(getMatch().getWorld().getWorldBorder(), getMatch().runningTime().minus(appliedAt));
        }
    }

    /**
     * If the current border is moving, stop it in-place
     */
    private void freeze() {
        if(appliedBorder != null && appliedBorder.isMoving()) {
            logger.fine("Freezing border " + appliedBorder);
            getMatch().getWorld().getWorldBorder().setSize(getMatch().getWorld().getWorldBorder().getSize(), 0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoalComplete(GoalEvent event) {
        update();
    }

//    /**
//     * Prevent teleporting outside the border
//     */
//    @EventHandler(priority = EventPriority.HIGH)
//    public void onPlayerTeleport(final PlayerTeleportEvent event) {
//        if(event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN && getMatch().hasStarted()) {
//            Vector center = appliedBorder != null ? appliedBorder.center : getMatch().getWorld().getWorldBorder().getCenter().toVector();
//            double size = appliedBorder != null ? appliedBorder.size : getMatch().getWorld().getWorldBorder().getSize();
//            if(WorldBorderUtils.isInsideBorder(center, size, event.getFrom()) &&
//               !WorldBorderUtils.isInsideBorder(center, size, event.getTo())) {
//                event.setCancelled(true);
//            }
//        }
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        MatchPlayer player = getMatch().getPlayer(event.getPlayer());
        if(player != null && !player.isObserving() && bedrock && getMatch().hasStarted()) {
            Location location = event.getTo();
            Vector center = appliedBorder != null ? appliedBorder.center : getMatch().getWorld().getWorldBorder().getCenter().toVector();
            double size = appliedBorder != null ? appliedBorder.size : getMatch().getWorld().getWorldBorder().getSize();
            if(WorldBorderUtils.clampToBorder(center, size - 0.2, location)) {
                event.setTo(location);
            }

            int GLASS_RADIUS = 8;
            for (int x = -GLASS_RADIUS; x <= GLASS_RADIUS; x++) {
                for (int z = -GLASS_RADIUS; z <= GLASS_RADIUS; z++) {
                    int potentialX = location.getBlockX() + x;
                    int potentialZ = location.getBlockZ() + z;
                    if (WorldBorderUtils.isOnBorder(center, size, potentialX, potentialZ)) {
                        for (int y = -GLASS_RADIUS; y <= GLASS_RADIUS; y++) {
                            int potentialY = location.getBlockY() + y;
                            Location blockLocation = new Location(getMatch().getWorld(), potentialX, potentialY, potentialZ);


                            if (blockLocation.getBlock().getType().equals(Material.AIR)) {
                                double distance = location.distance(blockLocation);

                                Material material = (distance > GLASS_RADIUS - 2) ? Material.AIR : Material.STAINED_GLASS;
                                byte data = (distance > GLASS_RADIUS - 2) ? (byte)0 : (byte)14;
                                if (!(material.equals(Material.STAINED_GLASS) && Math.abs(y) > 2)) {
                                    player.getBukkit().sendBlockChange(blockLocation, material, data);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isOnBorder(Vector center, double size, int x, int z) {
        double radius = (size / 2d);
        return (Math.abs(x - center.getX()) == radius ||
                Math.abs(z - center.getZ()) == radius) &&
                (Math.abs(x - center.getX()) <= radius &&
                        Math.abs(z - center.getZ()) <= radius);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        MatchPlayer player = getMatch().getPlayer(event.getPlayer());
        if(player != null && bedrock) {
            Location location = event.getBlockPlaced().getLocation();
            Vector center = appliedBorder != null ? appliedBorder.center : getMatch().getWorld().getWorldBorder().getCenter().toVector();
            double size = appliedBorder != null ? appliedBorder.size : getMatch().getWorld().getWorldBorder().getSize();
            if(WorldBorderUtils.clampToBorder(center, size - 0.2, location)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        MatchPlayer player = getMatch().getPlayer(event.getPlayer());
        if(player != null && bedrock) {
            Location location = event.getBlock().getLocation();
            Vector center = appliedBorder != null ? appliedBorder.center : getMatch().getWorld().getWorldBorder().getCenter().toVector();
            double size = appliedBorder != null ? appliedBorder.size : getMatch().getWorld().getWorldBorder().getSize();
            if(WorldBorderUtils.clampToBorder(center, size - 0.2, location)) {
                event.setCancelled(true);
            }
        }
    }
}
