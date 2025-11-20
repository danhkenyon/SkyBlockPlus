package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.WorldType;

/**
 * Represents the specific environment of a world in the server.
 * This enum associates a world type with its corresponding environmental context,
 * providing distinct values for each available world environment.
 *
 * Enum constants:
 * - NORMAL: Represents the standard overworld environment.
 * - NETHER: Represents the nether environment, commonly referred to as the underworld.
 * - THE_END: Represents the end dimension, typically used for end-game content.
 */
public enum WorldEnvironment {
    NORMAL,
    NETHER,
    THE_END;

    public WorldType toBukkit() {
        return WorldType.valueOf(toString().toUpperCase());
    }
}
