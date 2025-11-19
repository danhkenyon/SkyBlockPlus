package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.World;
import uk.ac.bsfc.sbp.utils.location.SBWorld;

public class IslandWorld extends SBWorld {
    private final int islandAmount;

    protected IslandWorld(String name) {
        super(
                name,
                World.Environment.NORMAL.name(),
                0,
                true
        );
    }


}
