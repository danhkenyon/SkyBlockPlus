package uk.ac.bsfc.sbp.core.spawners;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.util.*;

public class SpawnerStackManager {

    private final SpawnerDAO dao;
    private final Map<BlockPosKey, SpawnerData> cache = new HashMap<>();

    public SpawnerStackManager(SpawnerDAO dao) {
        this.dao = dao;
    }

    public void loadAll(List<SpawnerData> dataList) {
        for (SpawnerData data : dataList) {
            cache.put(BlockPosKey.of(data.getLocation()), data);
        }
        SBLogger.info("[SBP] Loaded " + cache.size() + " spawner stacks.");
    }

    public Optional<SpawnerData> get(Location loc) {
        return Optional.ofNullable(cache.get(BlockPosKey.of(loc)));
    }

    public int getStackSize(Location loc) {
        SpawnerData data = cache.get(BlockPosKey.of(loc));
        return data == null ? 0 : data.getStackSize();
    }

    public void add(SpawnerData data) {
        cache.put(BlockPosKey.of(data.getLocation()), data);
        dao.saveSpawner(data);
    }

    public void remove(Location loc) {
        BlockPosKey key = BlockPosKey.of(loc);
        cache.remove(key);
        dao.deleteSpawner(loc);
    }

    public Collection<SpawnerData> getAll() {
        return cache.values();
    }
}
