package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;

import java.util.List;

public class SBFlatWorld extends SBWorld {
    private final List<FlatWorldGenerator.Layer> layers;
    private final FlatWorldGenerator worldGenerator;

    public SBFlatWorld(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers, SBLocation spawnLoc) {
        super(name, env, seed, spawnLoc);
        this.layers = layers;
        this.worldGenerator = new FlatWorldGenerator(layers);
    }
    public SBFlatWorld(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers) {
        this(name, env, seed, layers, null);
    }

    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(this.getName())
                .environment(org.bukkit.World.Environment.valueOf(getEnvironment().name()))
                .seed(getSeed())
                .type(WorldType.FLAT)
                .generator(this.getWorldGenerator());
    }

    public static SBFlatWorld create(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers) {
        SBFlatWorld world = new SBFlatWorld(name, env, seed, layers);
        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
    public static SBFlatWorld create(String name, WorldEnvironment env, long seed, FlatWorldGenerator.Layer ... layers) {
        SBFlatWorld world = new SBFlatWorld(name, env, seed, List.of(layers));
        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }

    public List<FlatWorldGenerator.Layer> getLayers() {
        return layers;
    }
    public FlatWorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public boolean generateStructures() {
        return this.getWorldGenerator().generateStructures();
    }
    public SBFlatWorld generateStructures(boolean generateStructures) {
        this.getWorldGenerator().generateStructures(generateStructures);
        return this;
    }
    public boolean generateCaves() {
        return this.getWorldGenerator().generateCaves();
    }
    public SBFlatWorld generateCaves(boolean generateCaves) {
        this.getWorldGenerator().generateCaves(generateCaves);
        return this;
    }
    public boolean generateDecorations() {
        return this.getWorldGenerator().generateDecorations();
    }
    public SBFlatWorld generateDecorations(boolean generateDecorations) {
        this.getWorldGenerator().generateDecorations(generateDecorations);
        return this;
    }
}
