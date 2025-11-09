package uk.ac.bsfc.sbp.core.skyblock;

import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.schematic.Region;

public class IslandRegion extends Region {
    protected IslandRegion(SBLocation start) {
        super(
                start,
                SBLocation.of(
                        start.clone().toBukkit().add(
                                SBConstants.Island.BASE_ISLAND_SIZE,
                                256,
                                SBConstants.Island.BASE_ISLAND_SIZE
                        )
                )
        );
    }

    public static IslandRegion of(SBLocation start) {
        return new IslandRegion(start);
    }
}
