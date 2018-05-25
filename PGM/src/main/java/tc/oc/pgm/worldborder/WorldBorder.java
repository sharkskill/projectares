package tc.oc.pgm.worldborder;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import java.time.Duration;
import tc.oc.commons.bukkit.util.Vectors;
import tc.oc.commons.bukkit.util.WorldBorderUtils;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.filters.matcher.StaticFilter;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parameters for the world border.
 */
public class WorldBorder {
    final Filter filter;                // State should only be applied when a MatchQuery passes this filter
    final Vector center;                // Center of the border
    final double size;                  // Diameter of the border
    final Duration duration;            // Time taken to transition into this state from any previous state
    final double damage;                // Damage per second dealt to players for each meter outside of the border the are located
    final double buffer;                // Distance from the edge of the border where the damage to players begins
    final double warningDistance;       // Show red vignette to players closer than this to border
    final Duration warningTime;         // Show red vignette to players when the border is moving and will reach them within this time
    final boolean teleport;             // Teleport players who are outside of the border
    final boolean bedrock;              // Use a bedrock border

    public WorldBorder(Filter filter, Vector center, double size, Duration duration, double damage, double buffer, double warningDistance, Duration warningTime, boolean teleport, boolean bedrock) {
        this.filter = checkNotNull(filter);
        this.center = checkNotNull(center);
        this.size = size;
        this.duration = checkNotNull(duration);
        this.damage = damage;
        this.buffer = buffer;
        this.warningDistance = warningDistance;
        this.warningTime = checkNotNull(warningTime);
        this.teleport = teleport;
        this.bedrock = bedrock;
    }

    public double getSize() {
        return size;
    }

    public boolean isMoving() {
        return !Duration.ZERO.equals(duration);
    }

    public boolean isConditional() {
        return !StaticFilter.ALLOW.equals(filter);
    }

    public void apply(Match match, org.bukkit.WorldBorder bukkit, boolean transition, double oldSize) {
        if (this.bedrock) {
            bukkit.reset();
            World world = match.getWorld();
            int lowerX = (int)center.getX() - (int)(size/2) - 1;
            int upperX = (int)center.getX() + (int)(size/2);
            int lowerZ = (int)center.getZ() - (int)(size/2) - 1;
            int upperZ = (int)center.getZ() + (int)(size/2);
            for (int x = lowerX + 1; x < upperX; x++) {
                int highestLowerBlockY = world.getHighestBlockYAt(x, lowerZ);
                int highestUpperBlockY = world.getHighestBlockYAt(x, upperZ);
                for (int y = -3; y <= 6; y++) {
                    world.getBlockAt(x, highestLowerBlockY + y, lowerZ).setType(Material.BEDROCK);
                    world.getBlockAt(x, highestUpperBlockY + y, upperZ).setType(Material.BEDROCK);
                }
            }
            for (int z = lowerZ + 1; z < upperZ; z++) {
                int highestLowerBlockY = world.getHighestBlockYAt(lowerX, z);
                int highestUpperBlockY = world.getHighestBlockYAt(upperX, z);
                for (int y = -3; y <= 6; y++) {
                    world.getBlockAt(lowerX, highestLowerBlockY + y, z).setType(Material.BEDROCK);
                    world.getBlockAt(upperX, highestUpperBlockY + y, z).setType(Material.BEDROCK);
                }
            }
        } else {
            bukkit.setDamageAmount(damage);
            bukkit.setDamageBuffer(buffer);
            bukkit.setWarningDistance((int) Math.round(warningDistance));
            bukkit.setWarningTime((int) warningTime.getSeconds());
            bukkit.setCenter(center.getX(), center.getZ());

            if(transition && isMoving()) {
                bukkit.setSize(size, Math.max(0, duration.getSeconds()));
            } else {
                bukkit.setSize(size);
            }
        }

        if (this.teleport) {
            for (MatchPlayer player : match.getParticipatingPlayers()) {
                if (player.isDead()) {
                    continue;
                }
                Location location = player.getLocation();
                if (!WorldBorderUtils.isInsideBorder(center, size, location)) {
                    location.setX(location.getX() * (size/oldSize) * 0.99);
                    location.setZ(location.getZ() * (size/oldSize) * 0.99);
                    Block highestBlock = match.getWorld().getHighestBlockAt(location);
                    Location highestBlockLocation = highestBlock.getLocation();
                    highestBlockLocation.subtract(0, 1, 0);
                    highestBlockLocation.getBlock().setType(Material.SMOOTH_BRICK);
                    location.setY(highestBlockLocation.getY() + 1);

                    player.getBukkit().teleport(location);
                    player.sendMessage(ChatColor.YELLOW + PGMTranslations.get().t("border.teleport", player.getBukkit()));
                }
            }
        }
    }

    public void refresh(org.bukkit.WorldBorder bukkit, Duration elapsed) {
        if(isMoving()) {
            bukkit.setSize(size, Math.max(0, duration.minus(elapsed).getSeconds()));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{center=" + Vectors.format(center) +
               " size=" + size +
               (isMoving() ? " duration=" + duration : "") +
               (isConditional() ? " filter=" + filter : "") +
               "}";
    }
}
