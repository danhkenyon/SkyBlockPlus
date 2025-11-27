package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.EndWorldGenerator;

public class SBEndWorld extends SBWorld {
    public SBEndWorld(String name, long seed) {
        super(name, WorldEnvironment.THE_END, seed);
    }

    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(this.getName())
                .environment(org.bukkit.World.Environment.THE_END)
                .seed(getSeed())
                .generator(new EndWorldGenerator());
    }

    public static SBEndWorld create(String name, long seed) {
        SBEndWorld world = new SBEndWorld(name, seed);
        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }

    public static SBEndWorld create(String name) {
        return create(name, System.currentTimeMillis());
    }
}