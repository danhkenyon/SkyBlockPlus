package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.schematic.Region;

public class IslandRegion extends Region {
    protected IslandRegion(Location start) {
        super(
                start,
                start.clone().add(SBConstants.Island.BASE_ISLAND_SIZE, 256, SBConstants.Island.BASE_ISLAND_SIZE)
        );
    }

    public static IslandRegion of(Location start) {
        return new IslandRegion(start);
    }
}
