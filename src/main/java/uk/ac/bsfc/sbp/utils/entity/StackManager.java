package uk.ac.bsfc.sbp.utils.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StackManager {

    private final Map<UUID, SBEntity> entities = new HashMap<>();

    public SBMob spawnStackedMob(Location loc, EntityType type, int stackSize) {
        LivingEntity mob = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        SBMob sbMob = new SBMob(mob, stackSize);
        entities.put(mob.getUniqueId(), sbMob);
        return sbMob;
    }

    public SBEntity get(UUID uuid) {
        return entities.get(uuid);
    }

    public void remove(UUID uuid) {
        SBEntity sb = entities.remove(uuid);
        if (sb != null) sb.kill();
    }

    public void killAll() {
        entities.values().forEach(SBEntity::kill);
        entities.clear();
    }

    public Map<UUID, SBEntity> getAll() {
        return entities;
    }
}

