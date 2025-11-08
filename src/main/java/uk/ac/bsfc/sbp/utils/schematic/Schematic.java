package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import uk.ac.bsfc.sbp.utils.location.SBLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Schematic transform(Rotation rotation, Mirror mirror) {
        List<BlockEntry> transformed = new ArrayList<>();

        for (BlockEntry entry : blocks) {
            Vector pos = SchematicPlacer.transform(entry.pos(), rotation, mirror);
            BlockData newData = SchematicPlacer.transformBlockData(entry.blockData(), rotation, mirror);
            Map<String, Object> nbt = entry.nbt();

            transformed.add(new BlockEntry(pos, entry.type(), newData, nbt));
        }

        return new Schematic(this.name() + "_transformed", this.origin(), transformed);
    }

    private Vector rotateVector(Vector v, Rotation rotation) {
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        return switch (rotation) {
            case CLOCKWISE_90 -> new Vector(-z, y, x);
            case CLOCKWISE_180 -> new Vector(-x, y, -z);
            case CLOCKWISE_270 -> new Vector(z, y, -x);
            default -> v.clone();
        };
    }

    @Override
    public void paste(SBLocation loc) {
        SchematicPlacer.place(this, loc.getWorld(), loc, Rotation.NONE, Mirror.NONE);
    }
}