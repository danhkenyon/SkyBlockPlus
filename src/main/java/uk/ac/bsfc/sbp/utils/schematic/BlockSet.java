package uk.ac.bsfc.sbp.utils.schematic;

import uk.ac.bsfc.sbp.utils.location.SBLocation;

/**
 * Represents an abstract class for managing a set of blocks in a 3D space.
 * This class provides the core functionality for working with collections
 * of blocks and includes methods for interacting with their placement in the
 * world. Subclasses are expected to provide concrete implementations for
 * specific block set functionalities.
 */
public abstract class BlockSet {
    public abstract void paste(SBLocation loc);
}
