package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a three-dimensional rectangular region defined by two corner
 * locations in the world. This region supports several operations such as
 * checking whether a location is inside the region, filling the region with
 * a specific material, and copying blocks within the region to a schematic.
 *
 * The two corner locations, referred to as `loc1` (one corner) and `loc2`
 * (opposite corner), define the boundaries of the region in the world.
 * These corner locations are optional and can be null. The region is considered
 * incomplete until both locations are set.
 *
 * This class provides methods to:
 * - Check if a location is inside the region.
 * - Check if the region is complete.
 * - Synchronously or asynchronously fill the region with a material.
 * - Copy the blocks within the region into a schematic.
 * - Set and retrieve the corner locations.
 *
 * The region can also be pasted into a different location in the world
 * by placing the copied schematic.
 */
public class Region extends BlockSet {
    private @Nullable SBLocation loc1;
    private @Nullable SBLocation loc2;

    protected Region(@Nullable SBLocation loc1, @Nullable SBLocation loc2) {
        this.loc1 = loc1 == null ? null : loc1.clone();
        this.loc2 = loc2 == null ? null : loc2.clone();

        if (this.loc1 != null && this.loc2 != null) {
            if (this.loc1.getWorld() != this.loc2.getWorld()) {
                throw new IllegalArgumentException("Region corners must be in the same world.");
            }
        }
    }

    public static Region of(@Nullable SBLocation loc1, @Nullable SBLocation loc2) {
        return new Region(loc1, loc2);
    }

    public boolean isInside(SBLocation loc) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        if (!loc.getWorld().equals(loc1.getWorld())) {
            return false;
        }
        double x = loc.getX(),
                y = loc.getY(),
                z = loc.getZ();
        return x >= loc1.getX() && x <= loc2.getX()
                && y >= loc1.getY() && y <= loc2.getY()
                && z >= loc1.getZ() && z <= loc2.getZ();
    }
    public boolean isComplete() {
        return loc1 != null || loc2 != null;
    }
    public void fill(Material material) {
        if (loc1 == null || loc2 == null) {
            return;
        }
        if (material == null) {
            return;
        }

        SBWorld world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            return;
        }

        int[] values = SchematicParser.getCoordinates(loc1, loc2);
        int filled = 0;
        for (int x = values[0]; x <= values[1]; x++) {
            for (int y = values[2]; y <= values[3]; y++) {
                for (int z = values[4]; z <= values[5]; z++) {
                    Block block = world.toBukkit().getBlockAt(x, y, z);
                    if (block.getType() != material) {
                        block.setType(material, false);
                        filled++;
                    }
                }
            }
        }

        SBLogger.raw("[SkyBlockPlus] Filled region with " + filled + " blocks of " + material.name());
    }
    public void asyncFill(Material material) {
        if (loc1 == null || loc2 == null || material == null || material == Material.AIR) {
            return;
        }

        SBWorld world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            return;
        }

        int[] values = SchematicParser.getCoordinates(loc1, loc2);
        new Thread(() -> {
            List<Block> blocksToFill = new ArrayList<>();

            for (int x = values[0]; x <= values[1]; x++) {
                for (int y = values[2]; y <= values[3]; y++) {
                    for (int z = values[4]; z <= values[5]; z++) {
                        Block block = world.toBukkit().getBlockAt(x, y, z);
                        if (block.getType() != material) {
                            blocksToFill.add(block);
                        }
                    }
                }
            }
            int filledCount = blocksToFill.size();

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block block : blocksToFill) {
                        block.setType(material, false);
                    }
                    SBLogger.raw("[SkyBlockPlus] Filled region asynchronously with " + filledCount + " blocks of " + material.name());
                }
            }.runTask(Main.getInstance());

        }).start();
    }
    public Schematic copy() {
        if (loc1 == null || loc2 == null) {
            SBLogger.err("Cannot copy region: loc1 or loc2 is null.");
            return null;
        }

        SBWorld world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            SBLogger.err("Cannot copy region: world is null or mismatched.");
            return null;
        }

        int[] values = SchematicParser.getCoordinates(loc1, loc2);
        List<BlockEntry> blocks = new ArrayList<>();

        for (int x = values[0]; x <= values[1]; x++) {
            for (int y = values[2]; y <= values[3]; y++) {
                for (int z = values[4]; z <= values[5]; z++) {
                    Block block = world.toBukkit().getBlockAt(x, y, z);
                    Material mat = block.getType();
                    if (mat == Material.AIR) continue;

                    BlockData data = block.getBlockData();

                    blocks.add(new BlockEntry(
                            new Vector(x - values[0], y - values[2], z - values[4]),
                            mat,
                            data,
                            Map.of()
                    ));
                }
            }
        }

        return new Schematic(this.toString(), new Vector(0, 0, 0), blocks);
    }

    public void setLoc1(@NotNull SBLocation loc1) {
        this.loc1 = loc1;
        if (this.loc2 != null) {
            if (this.loc1.getWorld() != this.loc2.getWorld()) {
                throw new IllegalArgumentException("Region corners must be in the same world.");
            }
        }
    }
    public void setLoc2(@NotNull SBLocation loc2) {
        this.loc2 = loc2;
        if (this.loc1 != null) {
            if (this.loc1.getWorld() != this.loc2.getWorld()) {
                throw new IllegalArgumentException("Region corners must be in the same world.");
            }
        }
    }

    public @Nullable SBLocation getLoc1() {
        return this.loc1;
    }
    public @Nullable SBLocation getLoc2() {
        return this.loc2;
    }

    @Override
    public void paste(SBLocation loc) {
        SchematicPlacer.place(this.copy(), loc.getWorld(), loc, Rotation.NONE, Mirror.NONE);
    }
    
    @Override
    public String toString() {
        return "Region[loc1=" + loc1 + ", loc2=" + loc2 + ']';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region that)) return false;
        return Objects.equals(hashCode(), that.hashCode());
    }
    @Override
    public int hashCode() {
        return 31 * (loc1 != null ? loc1.hashCode() : 0) + (loc2 != null ? loc2.hashCode() : 0);
    }
}
