package uk.ac.bsfc.sbp.utils.npc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class FakeEntity {
    private final EntityType<? extends Entity> type;
    private final Level level;

    FakeEntity(EntityType<? extends Entity> type, Level level) {
        this.type = type;
        this.level = level;
    }

    public static Entity create(EntityType<? extends Entity> type, Level level) {
        FakeEntity entity = new FakeEntity(type, level);
        return entity.type.create(entity.level, EntitySpawnReason.MOB_SUMMONED);
    }
}