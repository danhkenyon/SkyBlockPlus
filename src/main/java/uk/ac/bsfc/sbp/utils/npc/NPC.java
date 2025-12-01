package uk.ac.bsfc.sbp.utils.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class NPC extends FakeEntity {
    public NPC(Level level) {
        super(EntityType.VILLAGER, level);
    }
}
