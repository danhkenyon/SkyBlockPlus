package uk.ac.bsfc.sbp.utils.schematic;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Region extends BlockSet {
    private @Nullable Location loc1;
    private @Nullable Location loc2;

    protected Region(@Nullable Location loc1, @Nullable Location loc2) {
        this.loc1 = loc1 == null ? null : loc1.clone();
        this.loc2 = loc2 == null ? null : loc2.clone();
    }

    public static Region of(@Nullable Location loc1, @Nullable Location loc2) {
        return new Region(loc1, loc2);
    }

    public boolean isInside(Location loc) {
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
        if (loc1 == null || loc2 == null) {
            SBLogger.err("Region is not complete. loc1 or loc2 is null.");
        }
        return loc1 != null && loc2 != null;
    }
    public void fill(Material material) {
        if (loc1 == null || loc2 == null) {
            return;
        }
        if (material == null) {
            return;
        }

        World world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            return;
        }

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int filled = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
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

        World world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            return;
        }

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        new Thread(() -> {
            List<Block> blocksToFill = new ArrayList<>();

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
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

        World world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            SBLogger.err("Cannot copy region: world is null or mismatched.");
            return null;
        }

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        List<BlockEntry> blocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material mat = block.getType();
                    if (mat == Material.AIR) continue;

                    BlockData data = block.getBlockData();

                    blocks.add(new BlockEntry(
                            new org.bukkit.util.Vector(x - minX, y - minY, z - minZ),
                            mat,
                            data,
                            Map.of()
                    ));
                }
            }
        }

        return new Schematic(this.toString(), new org.bukkit.util.Vector(0, 0, 0), blocks);
    }


    public void setLoc1(@NotNull Location loc1) {
        this.loc1 = loc1;
    }
    public void setLoc2(@NotNull Location loc2) {
        this.loc2 = loc2;
    }

    public @Nullable Location getLoc1() {
        return this.loc1;
    }
    public @Nullable Location getLoc2() {
        return this.loc2;
    }

    @Override
    public String toString() {
        return "Region[loc1=" + loc1 + ", loc2=" + loc2 + ']';
    }

    @Override
    public void paste(Location loc) {
        SchematicPlacer.place(this.copy(), loc.getWorld(), loc, Rotation.NONE, Mirror.NONE);
    }
}
