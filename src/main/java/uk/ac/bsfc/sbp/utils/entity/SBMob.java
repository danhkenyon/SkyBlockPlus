package uk.ac.bsfc.sbp.utils.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class SBMob extends SBEntity {

    private final LivingEntity mob;

    public SBMob(LivingEntity entity, int initialStack) {
        super(entity, initialStack);
        this.mob = entity;
    }

    @Override
    protected String getDisplayName() {
        EntityType type = entity.getType();
        String name = type.name().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}