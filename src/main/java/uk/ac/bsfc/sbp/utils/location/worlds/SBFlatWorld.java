package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;

import java.util.List;

public class SBFlatWorld extends SBWorld {
    private final List<FlatWorldGenerator.Layer> layers;

    public SBFlatWorld(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers) {
        super(name, env, seed, false);
        this.layers = layers;
    }

    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(this.getName())
                .environment(org.bukkit.World.Environment.valueOf(getEnvironment().name()))
                .seed(getSeed())
                .type(WorldType.FLAT)
                .generator(new FlatWorldGenerator(layers));
    }

    public static SBFlatWorld create(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers) {
        SBFlatWorld world = new SBFlatWorld(name, env, seed, layers);
        world.load();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
    public static SBFlatWorld create(String name, WorldEnvironment env, long seed, FlatWorldGenerator.Layer ... layers) {
        SBFlatWorld world = new SBFlatWorld(name, env, seed, List.of(layers));
        world.load();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
}
