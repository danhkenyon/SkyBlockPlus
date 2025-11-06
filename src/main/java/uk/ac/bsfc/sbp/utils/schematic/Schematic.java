package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import uk.ac.bsfc.sbp.utils.location.SBLocation;

import java.util.List;

public class Schematic extends BlockSet {
    private final String name;
    private final Vector origin;
    private final List<BlockEntry> blocks;

    public Schematic(String name, Vector origin, List<BlockEntry> blocks) {
        this.name = name;
        this.origin = origin;
        this.blocks = blocks;
    }

    public String name() {
        return name;
    }
    public Vector origin() {
        return origin;
    }
    public List<BlockEntry> blocks() {
        return blocks;
    }

    @Override
    public void paste(SBLocation loc) {
        SchematicPlacer.place(this, loc.getWorld(), loc, Rotation.NONE, Mirror.NONE);
    }
}