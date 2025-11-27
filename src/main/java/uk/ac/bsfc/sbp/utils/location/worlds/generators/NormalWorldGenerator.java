package uk.ac.bsfc.sbp.utils.location.worlds.generators;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class NormalWorldGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ,
            @NotNull ChunkData chunkData
    ) {
        // TODO: i dont wanna do it someone else do it please (preferably sage because shes done it before)
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return List.of();
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0.5, world.getMinHeight() + 68, 0.5);
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }
}