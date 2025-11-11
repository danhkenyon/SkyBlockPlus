package uk.ac.bsfc.sbp.utils.schematic;

/**
 * Represents types of 90-degree rotation transformations that can be applied.
 * Rotation involves turning an object around its center point in the specified
 * direction and number of degrees. Each constant corresponds to a specific degree
 * of clockwise rotation:
 *
 * - NONE: No rotation is applied.
 * - CLOCKWISE_90: Rotates the object by 90 degrees in the clockwise direction.
 * - CLOCKWISE_180: Rotates the object by 180 degrees.
 * - CLOCKWISE_270: Rotates the object by 270 degrees in the clockwise direction.
 *
 * This enum is typically used in contexts where rotation transformations
 * are applied to objects, such as schematic positioning and object rendering.
 */
public enum Rotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270
}