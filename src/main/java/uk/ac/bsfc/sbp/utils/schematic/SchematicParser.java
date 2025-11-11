package uk.ac.bsfc.sbp.utils.schematic;

import com.google.gson.*;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The SchematicParser class provides functionality for loading, saving,
 * and managing schematic files. Schematics represent collections of blocks,
 * their metadata, and positional data within a specific structure format.
 * This class supports both synchronous and asynchronous operations for
 * handling schematic files.
 *
 * Key operations include:
 * - Parsing and loading schematics from files.
 * - Saving schematics to JSON-based files.
 * - Asynchronous wrapper methods to offload resource-intensive operations.
 */
public class SchematicParser {
    static final Gson GSON = new Gson();

    @SuppressWarnings("unchecked")
    public static Schematic load(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            String name = root.get("name").getAsString();
            Vector origin = SchematicParser.parseVector(root.getAsJsonArray("origin"));

            List<BlockEntry> blocks = new ArrayList<>();
            JsonArray blockArray = root.getAsJsonArray("blocks");

            for (JsonElement el : blockArray) {
                JsonObject obj = el.getAsJsonObject();

                Vector pos = SchematicParser.parseVector(obj.getAsJsonArray("pos"));
                Material mat = Material.matchMaterial(obj.get("type").getAsString());
                if (mat == null) {
                    SBLogger.warn("Unknown material: " + obj.get("type").getAsString());
                    continue;
                }
                if (mat == Material.AIR) continue;

                BlockData data = null;
                if (obj.has("blockData"))
                    data = Bukkit.createBlockData(mat, obj.get("blockData").getAsString());

                Map<String, Object> nbt = null;
                if (obj.has("nbt"))
                    nbt = GSON.fromJson(obj.getAsJsonObject("nbt"), Map.class);

                blocks.add(new BlockEntry(pos, mat, data, nbt));
            }

            return new Schematic(name, origin, blocks);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void save(Region region, @Nullable String name) {
        try {
            SBLocation loc1 = region.getLoc1();
            SBLocation loc2 = region.getLoc2();

            assert loc1 != null;
            assert loc2 != null;

            SBWorld world = loc1.getWorld();
            if (world == null) {
                SBLogger.warn("Region world is null. Cannot save schematic.");
                return;
            }

            String schematicName = name != null ? name : SBConstants.Schematics.DEFAULT_SCHEMATIC_NAME;
            int[] values = SchematicParser.getCoordinates(loc1, loc2);
            JsonArray blockArray = new JsonArray();

            for (int x = values[0]; x <= values[1]; x++) {
                for (int y = values[2]; y <= values[3]; y++) {
                    for (int z = values[4]; z <= values[5]; z++) {
                        Block block = world.toBukkit().getBlockAt(x, y, z);
                        Material mat = block.getType();

                        if (mat == Material.AIR) continue;
                        JsonObject blockObj = new JsonObject();

                        JsonArray pos = new JsonArray();
                        pos.add(x - values[0]);
                        pos.add(y - values[2]);
                        pos.add(z - values[4]);
                        blockObj.add("pos", pos);

                        blockObj.addProperty("type", "minecraft:" + mat.name().toLowerCase(Locale.ROOT));

                        String dataStr = block.getBlockData().getAsString();
                        int bracketIndex = dataStr.indexOf('[');
                        if (bracketIndex != -1) {
                            String stateStr = dataStr.substring(bracketIndex);
                            blockObj.addProperty("blockData", stateStr);
                        }

                        BlockState state = block.getState();
                        if (state instanceof TileState) {
                            try {
                                NBTBlock nbtBlock = new NBTBlock(block);
                                NBTCompound data = nbtBlock.getData();
                                if (data != null && !data.getKeys().isEmpty()) {
                                    JsonObject nbtJson = JsonParser.parseString(data.toString()).getAsJsonObject();
                                    blockObj.add("nbt", nbtJson);
                                }
                            } catch (Exception e) {
                                SBLogger.warn("Failed to get NBT for " + block.getType() + " at " + x + "," + y + "," + z);
                            }
                        }

                        blockArray.add(blockObj);
                    }
                }
            }
            JsonObject root = new JsonObject();
            root.addProperty("name", schematicName);

            JsonArray origin = new JsonArray();
            origin.add(0);
            origin.add((int) loc1.getY());
            origin.add(0);

            root.add("origin", origin);
            root.add("blocks", blockArray);

            File dir = new File("plugins/SkyBlockPlus/schematics");
            if (!dir.exists()) dir.mkdirs();

            File outFile = new File(dir, schematicName + ".json");
            try (FileWriter writer = new FileWriter(outFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(root, writer);
                writer.flush();
            }

            SBLogger.info("Saved schematic '" + schematicName + "' with " + blockArray.size() + " blocks at " + outFile.getPath());
        } catch (Exception e) {
            SBLogger.err("Failed to save schematic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Schematic asyncLoad(File file) {
        final Schematic[] schematic = new Schematic[1];
        Thread thread = new Thread(() -> schematic[0] = load(file), "Schematic-Loader");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return schematic[0];
    }
    public static Schematic asyncLoad(SBUser user, File file) {
        final Schematic[] schematic = new Schematic[1];
        Thread thread = new Thread(() -> schematic[0] = load(file), "Schematic-Loader");
        user.sendMessage("<yellow>Loading schematic...");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return schematic[0];
    }
    public static void asyncSave(Region region, @Nullable String name) {
        new Thread(() -> {
            try {
                SBLogger.info("Starting async save for schematic '" + name + "'...");
                save(region, name);
            } catch (Exception e) {
                SBLogger.err("Async schematic save failed: " + e.getMessage());
                e.printStackTrace();
            }
        }, "SchematicSaveThread-" + name).start();
    }
    public static void asyncSave(SBUser user, Region region, String name) {
        if (name == null || name.isEmpty()) {
            name = SBConstants.Schematics.DEFAULT_SCHEMATIC_NAME;
        }

        String finalName = name;
        new Thread(() -> {
            try {
                user.sendMessage("<yellow>Saving schematic...");
                save(region, finalName);
            } catch (Exception e) {
                SBLogger.err("Async schematic save failed: " + e.getMessage());
                e.printStackTrace();
            }
        }, "SchematicSaveThread-" + name).start();
    }

    private static Vector parseVector(JsonArray arr) {
        return new Vector(
                arr.get(0).getAsDouble(),
                arr.get(1).getAsDouble(),
                arr.get(2).getAsDouble()
        );
    }
    protected static int[] getCoordinates(SBLocation loc1, SBLocation loc2) {
        return new int[]{
                (int) Math.min(loc1.getX(), loc2.getX()),
                (int) Math.max(loc1.getX(), loc2.getX()),
                (int) Math.min(loc1.getY(), loc2.getY()),
                (int) Math.max(loc1.getY(), loc2.getY()),
                (int) Math.min(loc1.getZ(), loc2.getZ()),
                (int) Math.max(loc1.getZ(), loc2.getZ()),
        };
    }
}
