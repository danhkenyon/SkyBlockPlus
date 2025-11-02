package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Map;

public record BlockEntry(
        Vector pos,
        Material type,
        BlockData blockData,
        Map<String, Object> nbt
) {}