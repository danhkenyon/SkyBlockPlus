package uk.ac.bsfc.sbp.utils.location.worlds.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FlatWorldGenerator extends ChunkGenerator {
    private final List<Layer> layers;
    private boolean generateStructures = false;
    private boolean generateCaves = false;
    private boolean generateDecorations = false;

    public FlatWorldGenerator(List<Layer> layers) {
        this.layers = (layers == null) ? List.of() : layers;
    }
    public FlatWorldGenerator() {
        this.layers = List.of();
    }

    @Override
    public void generateSurface(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ,
            @NotNull ChunkData chunkData
    ) {
        int currentY = worldInfo.getMinHeight();

        for (Layer layer : layers) {
            for (int y = currentY; y < currentY + layer.thickness(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunkData.setBlock(x, y, z, layer.material());
                    }
                }
            }
            currentY += layer.thickness();
        }
    }

    public @Override boolean shouldGenerateNoise(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ
    ) {
        return false;
    }
    public @Override boolean shouldGenerateSurface(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ
    ) {
        return true;
    }
    public @Override boolean shouldGenerateCaves(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ
    ) {
        return this.generateCaves();
    }
    public @Override boolean shouldGenerateDecorations(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ
    ) {
        return this.generateDecorations();
    }
    public @Override boolean shouldGenerateStructures(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ
    ) {
        return this.generateStructures();
    }

    public @Override @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        int base = world.getMinHeight();
        int totalThickness = layers.stream().mapToInt(Layer::thickness).sum();
        int spawnY = base + totalThickness + 2;
        return new Location(world, 0.5, spawnY, 0.5);
    }
    public @Override @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return List.of();
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public boolean generateStructures() {
        return generateStructures;
    }
    public FlatWorldGenerator generateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
        return this;
    }
    public boolean generateCaves() {
        return generateCaves;
    }
    public FlatWorldGenerator generateCaves(boolean generateCaves) {
        this.generateCaves = generateCaves;
        return this;
    }
    public boolean generateDecorations() {
        return generateDecorations;
    }
    public FlatWorldGenerator generateDecorations(boolean generateDecorations) {
        this.generateDecorations = generateDecorations;
        return this;
    }

    public record Layer(Material material, int thickness) {}
}
