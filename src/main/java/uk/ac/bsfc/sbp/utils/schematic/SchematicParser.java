package uk.ac.bsfc.sbp.utils.schematic;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class SchematicParser {
    private static final Gson GSON = new Gson();

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

    private static Vector parseVector(JsonArray arr) {
        return new Vector(
                arr.get(0).getAsDouble(),
                arr.get(1).getAsDouble(),
                arr.get(2).getAsDouble()
        );
    }
}
