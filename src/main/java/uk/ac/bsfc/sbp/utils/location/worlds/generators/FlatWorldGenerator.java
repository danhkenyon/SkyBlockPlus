package uk.ac.bsfc.sbp.utils.location.worlds.generators;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.Random;

public class FlatWorldGenerator extends ChunkGenerator {
    private final List<Layer> layers;
    public FlatWorldGenerator(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public ChunkData generateChunkData(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = super.generateChunkData(worldInfo);
        int y = 0;
        for (Layer layer : layers) {
            for (int i = 0; i < layer.thickness(); i++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunkData.setBlock(x, y, z, layer.material());
                    }
                }
                y++;
            }
        }
        return chunkData;
    }

    public @Override boolean shouldGenerateNoise() {
        return false;
    }
    public @Override boolean shouldGenerateSurface() {
        return false;
    }
    public @Override boolean shouldGenerateCaves() {
        return false;
    }
    public @Override boolean shouldGenerateDecorations() {
        return false;
    }
    public @Override boolean shouldGenerateStructures() {
        return false;
    }

    public record Layer(Material material, int thickness) {}
}
