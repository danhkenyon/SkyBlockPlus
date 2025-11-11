package uk.ac.bsfc.sbp.utils.schematic;

/**
 * Enum representing different types of mirroring transformations that can be applied.
 * Mirroring refers to reflecting an object along a specific plane, creating a mirrored version.
 * This enum provides three strategies:
 *
 * NONE - No mirroring is applied.
 * LEFT_RIGHT - Indicates mirroring along a vertical plane that separates the left
 *              and right sides, creating a horizontal reflection.
 * FRONT_BACK - Indicates mirroring along a vertical plane that separates the front
 *              and back sides, creating a vertical reflection.
 */
public enum Mirror {
    NONE,
    LEFT_RIGHT,
    FRONT_BACK
}