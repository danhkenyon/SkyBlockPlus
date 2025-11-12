package uk.ac.bsfc.sbp.core.events;

import org.bukkit.Chunk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.entity.SBEntity;
import uk.ac.bsfc.sbp.utils.entity.SBMob;
import uk.ac.bsfc.sbp.utils.entity.StackManager;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;

import java.util.UUID;
//TODO:
/*
* Fix event being registered
* Put Config behind this
 */
public class MobStackerHandler extends SBEventHandler {

    private final StackManager stackManager = Main.getStackManager();

    @Event
    public void onMobStacker(SpawnerSpawnEvent event) {
        SBLogger.raw("<gray>Spawner spawned mob");
        LivingEntity entity = (LivingEntity) event.getEntity();
        Chunk chunk = entity.getChunk();

        SBMob existing = findExistingSameType(chunk, entity);

        if (existing != null) {
            event.setCancelled(true);
            existing.incrementStack(1);
            SBLogger.raw("<yellow>Stacked mob! New size: " + existing.getStackSize());
        } else {
            SBMob sbMob = new SBMob(entity, 1);
            stackManager.getAll().put(entity.getUniqueId(), sbMob);
            SBLogger.raw("<green>Created new stackable mob: " + entity.getType().name());
        }
    }

    private SBMob findExistingSameType(Chunk chunk, LivingEntity entity) {
        for (UUID uuid : stackManager.getAll().keySet()) {
            SBEntity sbEntity = stackManager.get(uuid);
            if (!(sbEntity instanceof SBMob sbMob)) continue;
            if (!sbMob.getEntity().getChunk().equals(chunk)) continue;
            if (sbMob.getEntity().getType() == entity.getType()) {
                return sbMob;
            }
        }
        return null;
    }
}
