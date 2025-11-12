package uk.ac.bsfc.sbp.utils.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class SBMob extends SBEntity {

    private final LivingEntity mob;

    public SBMob(LivingEntity entity, int initialStack) {
        super(entity, initialStack);
        this.mob = entity;
    }

    @Override
    protected String getDisplayName() {
        return mob.getType().name().replace("_", " ").toLowerCase();
    }
}
