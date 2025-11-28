package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.NetherWorldGenerator;

public class SBNetherWorld extends SBWorld {
    public SBNetherWorld(String name, long seed, SBLocation spawnLoc) {
        super(name, WorldEnvironment.NETHER, seed, spawnLoc);
    }
    public SBNetherWorld(String name, long seed) {
        this(name, seed, null);
    }

    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(this.getName())
                .environment(org.bukkit.World.Environment.NETHER)
                .seed(getSeed())
                .generator(new NetherWorldGenerator());
    }

    public static SBNetherWorld create(String name, long seed) {
        SBNetherWorld world = new SBNetherWorld(name, seed);
        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }

    public static SBNetherWorld create(String name) {
        return create(name, System.currentTimeMillis());
    }
}