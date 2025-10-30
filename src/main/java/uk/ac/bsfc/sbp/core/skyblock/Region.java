package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.Location;

public class Region {
    private final Island island;
    private final Location corner1;
    private final Location corner2;

    protected Region(Island island, Location start) {
        this.island = island;

        this.corner1 = start.clone();
        this.corner2 = start.clone().add(island.getSize(), 256, island.getSize());
    }

    public static Region of(Island island, Location start) {
        return new Region(island, start);
    }

    public boolean isInside(Location loc) {
        if (!loc.getWorld().equals(corner1.getWorld())) {
            return false;
        }
        double x = loc.getX(),
                y = loc.getY(),
                z = loc.getZ();
        return x >= corner1.getX() && x <= corner2.getX()
                && y >= corner1.getY() && y <= corner2.getY()
                && z >= corner1.getZ() && z <= corner2.getZ();
    }
    public Island getIsland() {
        return island;
    }
    public Location getLoc1() {
        return corner1;
    }
    public Location getLoc2() {
        return corner2;
    }
}
