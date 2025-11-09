package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import uk.ac.bsfc.sbp.utils.Wrapper;

public class SBLocation extends Wrapper<Location> implements Cloneable {
    private final String worldName;
    private final SBWorld world;
    private final double x, y, z;
    private final float yaw, pitch;

    /*
     * |>--<| CONSTRUCTORS |>--<|
     */

    private SBLocation(Location location) {
        this.world = SBWorld.getWorld(location.getWorld().getName());
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
    private SBLocation(SBWorld world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    private SBLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.world = SBWorld.getWorld(worldName);
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /*
     * |>--<| FACTORY METHODS |>--<|
     */

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
    public static SBLocation of(SBWorld world, double x, double y, double z, float yaw, float pitch) {
        return new SBLocation(world, x, y, z, yaw, pitch);
    }
    public static SBLocation of(SBWorld world, double x, double y, double z) {
        return new SBLocation(world, x, y, z, 0, 0);
    }
    public static SBLocation of(SBWorld world) {
        return new SBLocation(world, 0.5, 0, 0.5, 0, 0);
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

    /*
     * |>--<| GETTERS |>--<|
     */

    @Override
    public Location toBukkit() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }
    public SBWorld getWorld() {
        return world;
    }

    public double getX() { return x; }
    public double x() { return x; }
    public double getY() { return y; }
    public double y() { return y; }
    public double getZ() { return z; }
    public double z() { return z; }
    public float getYaw() { return yaw; }
    public float yaw() { return yaw; }
    public float getPitch() { return pitch; }
    public float pitch() { return pitch; }

    public String format() {
        return String.format("(%.0f, %.0f, %.0f)", x, y, z);
    }

    @Override
    public String toString() {
        return "SBLocation[world=" + world.getName() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch + "]";
    }
    @Override
    public SBLocation clone() {
        try {
            return (SBLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
