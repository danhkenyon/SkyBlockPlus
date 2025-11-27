package uk.ac.bsfc.sbp.utils.location.worlds;

import org.bukkit.Material;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;

import java.util.List;

public class SBVoidWorld extends SBFlatWorld {
    public SBVoidWorld(String name, WorldEnvironment env) {
        super(name, env, 0, List.of(new FlatWorldGenerator.Layer(Material.AIR, 1)));
    }
}
