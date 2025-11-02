package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.util.Vector;

import java.util.List;

public record Schematic(
        String name,
        Vector origin,
        List<BlockEntry> blocks
) {}