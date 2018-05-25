package tc.oc.commons.bukkit.util;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.util.Vector;

public class WorldBorderUtils {
    private WorldBorderUtils() {}

    public static boolean isInsideBorder(Location location) {
        return isInsideBorder(location.getWorld().getWorldBorder(), location);
    }

    public static boolean isInsideBorder(WorldBorder border, Location location) {
        return isInsideBorder(border.getCenter().toVector(), border.getSize(), location);
    }

    public static boolean isInsideBorder(Vector center, double size, Location location) {
        double radius = size / 2d;
        return Math.abs(location.getX() - center.getX()) < radius &&
               Math.abs(location.getZ() - center.getZ()) < radius;
    }

    public static boolean clampToBorder(Location location) {
        return clampToBorder(location.getWorld().getWorldBorder(), location);
    }

    public static boolean clampToBorder(WorldBorder border, Location location) {
        return clampToBorder(border.getCenter().toVector(), border.getSize(), location);
    }

    public static boolean clampToBorder(Vector center, double size, Location location) {
        double radius = size / 2d;
        double xMin = center.getX() - radius;
        double xMax = center.getX() + radius;
        double zMin = center.getZ() - radius;
        double zMax = center.getZ() + radius;

        boolean moved = false;

        if(location.getX() < xMin) {
            location.setX(xMin);
            moved = true;
        }

        if(location.getX() > xMax) {
            location.setX(xMax);
            moved = true;
        }

        if(location.getZ() < zMin) {
            location.setZ(zMin);
            moved = true;
        }

        if(location.getZ() > zMax) {
            location.setZ(zMax);
            moved = true;
        }

        return moved;
    }
}
