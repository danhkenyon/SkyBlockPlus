package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Represents the data of a block required for storing or reconstructing
 * its state and properties in a schematic or other serialized form.
 *
 * BlockEntry is an immutable record that stores positional, material,
 * block data, and optionally custom NBT (Named Binary Tag) data for a block.
 *
 * @param pos       The relative position of the block. The position is typically
 *                  specified relative to the origin of the schematic or region.
 * @param type      The material type of the block (e.g., stone, dirt, etc.).
 * @param blockData The block's specific data such as orientation, age, or other
 *                  state-specific properties.
 * @param nbt       A map of additional custom NBT data associated with the block,
 *                  which may include properties that are not covered by the block data.
 */
public record BlockEntry(
        Vector pos,
        Material type,
        BlockData blockData,
        Map<String, Object> nbt
) {}