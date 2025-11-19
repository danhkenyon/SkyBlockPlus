package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;

import java.util.List;

public class SBFlatWorld extends SBWorld {
    private final List<FlatWorldGenerator.Layer> layers;

    protected SBFlatWorld(String name, WorldEnvironment env, long seed, List<FlatWorldGenerator.Layer> layers) {
        super(name, env, seed, false);
        this.layers = layers;
    }

    @Override
    public World toBukkit() {
        World world = org.bukkit.Bukkit.getWorld(getName());
        if (world == null) {
            WorldCreator creator = new WorldCreator(getName())
                    .environment(org.bukkit.World.Environment.valueOf(getEnvironment().name()))
                    .seed(getSeed())
                    .type(WorldType.FLAT)
                    .generator(new FlatWorldGenerator(layers));

            world = creator.createWorld();
            setLoaded(world != null);
        }
        return world;
    }

    public static SBFlatWorld create(String name, String env, long seed, List<FlatWorldGenerator.Layer> layers) {
        SBFlatWorld world = new SBFlatWorld(name, env, seed, layers);
        world.load();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
}
