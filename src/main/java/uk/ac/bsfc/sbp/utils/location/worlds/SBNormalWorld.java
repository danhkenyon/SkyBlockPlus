package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.NormalWorldGenerator;

public class SBNormalWorld extends SBWorld {
    public SBNormalWorld(String name, long seed, SBLocation spawnLoc) {
        super(name, WorldEnvironment.NORMAL, seed, spawnLoc);
    }
    public SBNormalWorld(String name, long seed) {
        this(name, seed, null);
    }
    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(this.getName())
                .environment(org.bukkit.World.Environment.NORMAL)
                .seed(getSeed())
                .generator(new NormalWorldGenerator());
    }

    public static SBNormalWorld create(String name, long seed) {
        SBNormalWorld world = new SBNormalWorld(name, seed);
        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }

    public static SBNormalWorld create(String name) {
        return create(name, System.currentTimeMillis());
    }
}