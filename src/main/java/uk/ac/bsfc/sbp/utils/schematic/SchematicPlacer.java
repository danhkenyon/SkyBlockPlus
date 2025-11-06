package uk.ac.bsfc.sbp.utils.schematic;

import com.google.gson.Gson;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.*;
import org.bukkit.util.Vector;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;

import java.util.List;
import java.util.Map;

public class SchematicPlacer {
    private static final Gson GSON = new Gson();

    public static void place(Schematic schematic, SBWorld world, SBLocation base, Rotation rotation, Mirror mirror) {
        List<BlockEntry> blocks = schematic.blocks();

        for (BlockEntry entry : blocks) {
            Vector transformedPos = transform(entry.pos(), rotation, mirror);
            Location loc = base.clone().toBukkit().add(transformedPos);

            Block block = world.toBukkit().getBlockAt(loc);
            block.setType(entry.type(), false);

            BlockData data = entry.blockData();
            if (data != null) {
                data = transformBlockData(data, rotation, mirror);
                block.setBlockData(data, false);
            }

            if (entry.nbt() != null && !entry.nbt().isEmpty()) {
                applyNBT(block, entry.nbt());
            }
        }
    }

    private static Vector transform(Vector pos, Rotation rotation, Mirror mirror) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        switch (rotation) {
            case CLOCKWISE_90 -> { double t = x; x = -z; z = t; }
            case CLOCKWISE_180 -> { x = -x; z = -z; }
            case CLOCKWISE_270 -> { double t = x; x = z; z = -t; }
            default -> {}
        }

        switch (mirror) {
            case LEFT_RIGHT -> x = -x;
            case FRONT_BACK -> z = -z;
            default -> {}
        }

        return new Vector(x, y, z);
    }

    private static BlockData transformBlockData(BlockData data, Rotation rotation, Mirror mirror) {
        if (data instanceof Directional directional) {
            directional.setFacing(rotateFace(directional.getFacing(), rotation, mirror));
            return directional;
        }

        if (data instanceof Rotatable rotatable) {
            rotatable.setRotation(rotateFace(rotatable.getRotation(), rotation, mirror));
            return rotatable;
        }

        if (data instanceof Orientable orientable) {
            Axis axis = orientable.getAxis();
            axis = rotateAxis(axis, rotation, mirror);
            orientable.setAxis(axis);
            return orientable;
        }

        return data;
    }

    private static BlockFace rotateFace(BlockFace face, Rotation rotation, Mirror mirror) {
        if (face == BlockFace.UP || face == BlockFace.DOWN) return face;

        int steps = switch (rotation) {
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case CLOCKWISE_270 -> 3;
            default -> 0;
        };
        for (int i = 0; i < steps; i++) {
            face = switch (face) {
                case NORTH -> BlockFace.EAST;
                case EAST -> BlockFace.SOUTH;
                case SOUTH -> BlockFace.WEST;
                case WEST -> BlockFace.NORTH;
                default -> face;
            };
        }

        switch (mirror) {
            case LEFT_RIGHT -> {
                if (face == BlockFace.EAST) face = BlockFace.WEST;
                else if (face == BlockFace.WEST) face = BlockFace.EAST;
            }
            case FRONT_BACK -> {
                if (face == BlockFace.NORTH) face = BlockFace.SOUTH;
                else if (face == BlockFace.SOUTH) face = BlockFace.NORTH;
            }
            default -> {}
        }

        return face;
    }

    private static Axis rotateAxis(Axis axis, Rotation rotation, Mirror mirror) {
        if (axis == Axis.Y) return axis;

        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_270) {
            if (axis == Axis.X) axis = Axis.Z;
            else if (axis == Axis.Z) axis = Axis.X;
        }

        return axis;
    }

    private static void applyNBT(Block block, Map<String, Object> nbtMap) {
        if (nbtMap == null || nbtMap.isEmpty()) return;

        try {
            StringBuilder snbt = new StringBuilder("{");
            for (Map.Entry<String, Object> entry : nbtMap.entrySet()) {
                String value = entry.getValue().toString()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"");
                snbt.append(entry.getKey())
                        .append(":\"")
                        .append(value)
                        .append("\",");
            }
            snbt.deleteCharAt(snbt.length() - 1);
            snbt.append("}");

            NBTContainer container = (NBTContainer) NBT.parseNBT(String.valueOf(snbt));
            System.out.println(container);

            NBTBlock nbtBlock = new NBTBlock(block);

            nbtBlock.getData().addCompound(container.toString());
            System.out.println(nbtBlock.getData());
        } catch (Exception e) {
            SBLogger.err("Failed to apply NBT to block at " + block.getLocation() + ": " + e);
        }
    }
}
