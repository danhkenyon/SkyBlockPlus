package uk.ac.bsfc.sbp.core.events;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.entity.SBEntity;
import uk.ac.bsfc.sbp.utils.entity.SBMob;
import uk.ac.bsfc.sbp.utils.entity.StackManager;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobStackerHandler extends SBEventHandler {

    /*
    *TODO:
    * Add a dynamic component to the spawner count
    * add config allowing user to pick between having the stacker and not
     */

    private final StackManager stackManager = Main.getStackManager();
    private final Map<String, Long> spawnLock = new HashMap<>();

    @Event(async = false)
    public void onMobStacker(SpawnerSpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Chunk chunk = entity.getChunk();

        entity.setSilent(true);

        String key = chunk.getX() + "," + chunk.getZ() + ":" + entity.getType().name();
        long now = System.currentTimeMillis();

        if (spawnLock.containsKey(key) && (now - spawnLock.get(key)) < 50) {
            entity.remove();
            return;
        }
        spawnLock.put(key, now);

        SBMob existing = findExistingSameType(chunk, entity);
        int spawnCount = 1; // Could make dynamic later

        if (existing != null) {
            existing.incrementStack(spawnCount);
            entity.remove();
        } else {
            SBMob sbMob = new SBMob(entity, spawnCount);
            stackManager.getAll().put(entity.getUniqueId(), sbMob);
            if (/*disableMobAIConfig*/true){
                ((Mob) entity).setAware(false);
            }
        }
    }

    @Event(async = false)
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        SBEntity sbEntity = stackManager.get(entity.getUniqueId());
        if (!(sbEntity instanceof SBMob sbMob)) return;
        entity.setSilent(true);

        int current = sbMob.getStackSize();
        if (current > 1) {
            sbMob.decrementStack(1);
            SBLogger.raw("<red>Reduced stack size: " + sbMob.getStackSize());

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                entity.remove();

                LivingEntity newEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
                SBMob newStack = new SBMob(newEntity, sbMob.getStackSize());
                newEntity.setSilent(true);
                if (/*disableMobAIConfig*/true){
                    ((Mob) newEntity).setAware(false);
                }
                stackManager.getAll().put(newEntity.getUniqueId(), newStack);
                stackManager.remove(entity.getUniqueId());
            }, 1L);
        } else {
            stackManager.remove(entity.getUniqueId());
        }
    }

    private SBMob findExistingSameType(Chunk chunk, LivingEntity entity) {
        for (UUID uuid : stackManager.getAll().keySet()) {
            SBEntity sbEntity = stackManager.get(uuid);
            if (!(sbEntity instanceof SBMob sbMob)) continue;
            if (!sbMob.getEntity().getChunk().equals(chunk)) continue;
            if (sbMob.getEntity().getType() == entity.getType()) return sbMob;
        }
        return null;
    }
}
