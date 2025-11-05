package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SBLocation {
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    private SBLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
    private SBLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static SBLocation of(Location location) {
        return new SBLocation(location);
    }
    public static SBLocation of(String worldName, double x, double y, double z, float yaw, float pitch) {
        return new SBLocation(worldName, x, y, z, yaw, pitch);
    }
    public static SBLocation of(String worldName, double x, double y, double z) {
        return new SBLocation(worldName, x, y, z, 0, 0);
    }
    public static SBLocation of(String worldName) {
        return new SBLocation(worldName, 0.5, 0, 0.5, 0, 0);
    }
    public static SBLocation of(double x, double y, double z, float yaw, float pitch) {
        return new SBLocation("world", x, y, z, yaw, pitch);
    }
    public static SBLocation of(double x, double y, double z) {
        return new SBLocation("world", x, y, z, 0, 0);
    }
    public static SBLocation of() {
        return new SBLocation("world", 0.5, 0, 0.5, 0, 0);
    }

    public Location toBukkitLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }
    public String getWorldName() {
        return worldName;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
