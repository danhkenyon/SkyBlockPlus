package uk.ac.bsfc.sbp.utils.npc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class MobUtils {
    public static void disableAI(Entity e) {
        if (e instanceof Mob mob) {
            mob.setNoAi(true);
        }
    }
}
