package uk.ac.bsfc.sbp.core.skyblock;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.schematic.Region;

/**
 * Represents an IslandRegion in the system. This class is a specialized type of
 * {@link Region} that automatically calculates and sets the boundaries of the
 * region based on a starting location and predefined constants for dimensions.
 * An IslandRegion is used to define a configurable area typically associated with
 * an island's dimensions and is bounded in three-dimensional space.
 *
 * The region is constructed using a starting point and calculates its boundaries
 * by adding predefined values to the starting location.
 */
public class IslandRegion extends Region {
    protected IslandRegion(SBLocation start) {
        super(
                start,
                SBLocation.of(start.clone().toBukkit().add(
                        SBConstants.Island.BASE_ISLAND_SIZE,
                        256,
                        SBConstants.Island.BASE_ISLAND_SIZE
                ))
        );
    }

    public static IslandRegion of(SBLocation start) {
        return new IslandRegion(start);
    }

    public @Override @NotNull SBLocation getLoc1() {
        try {
            assert super.getLoc1() != null;
            return super.getLoc1();
        }catch(NullPointerException e) {
            throw new NullPointerException("IslandRegion loc1 is null. This may be due to an uninitialized region.");
        }
    }
    public @Override @NotNull SBLocation getLoc2() {
        try {
            assert super.getLoc2() != null;
            return super.getLoc2();
        }catch(NullPointerException e) {
            throw new NullPointerException("IslandRegion loc2 is null. This may be due to an uninitialized region.");
        }
    }
}
