package uk.ac.bsfc.sbp.core.spawners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class SpawnerData {
    private final UUID id;
    private final Location location;

    private int stackSize;
    private int level;
    private EntityType type;

    public SpawnerData(UUID id, Location location, EntityType type, int stackSize, int level) {
        this.id = id;
        this.location = location;
        this.stackSize = stackSize;
        this.level = level;
        this.type = type;
    }

    public UUID getId() { return id; }
    public Location getLocation() { return location; }

    public int getStackSize() { return stackSize; }
    public void setStackSize(int size) { this.stackSize = size; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public EntityType getType() { return type; }
    public void setType(EntityType type) { this.type = type; }
}

