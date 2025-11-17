package uk.ac.bsfc.sbp.core.spawners;

import org.bukkit.Location;

public record BlockPosKey(String world, int x, int y, int z) {

    public static BlockPosKey of(Location loc) {
        return new BlockPosKey(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );
    }
}
