package uk.ac.bsfc.sbp.core.events;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;
import uk.ac.bsfc.sbp.utils.entity.SBEntity;
import uk.ac.bsfc.sbp.utils.entity.SBMob;
import uk.ac.bsfc.sbp.utils.entity.StackManager;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;

import java.util.HashMap;
import java.util.Map;

public class MobStackerHandler extends SBEventHandler {

    private final StackManager stackManager = Main.getStackManager();
    private final Map<String, Long> spawnLock = new HashMap<>();

    @Event(async = false)
    public void onMobStacker(SpawnerSpawnEvent event) {
        FeatureConfig.MobStacker mobCfg = Main.getInstance().getConfig(FeatureConfig.class).mobStacker;
        if (!mobCfg.enabled) return;

        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Chunk chunk = entity.getChunk();

        entity.setSilent(true);
        entity.getPassengers().forEach(Entity::remove);

        String key = chunk.getX() + "," + chunk.getZ() + ":" + entity.getType().name();
        long now = System.currentTimeMillis();
        if (spawnLock.containsKey(key) && (now - spawnLock.get(key)) < 5) {
            entity.remove();
            return;
        }
        spawnLock.put(key, now);

        int spawnCount = getSpawnCountFromSpawner(event.getSpawner());

        long currentStacksInChunk = stackManager.getAll().values().stream()
                .filter(sb -> sb instanceof SBMob sbMob
                        && sbMob.getEntity().getChunk().equals(chunk)
                        && sbMob.getEntity().getType() == entity.getType())
                .count();

        if (currentStacksInChunk >= mobCfg.maxStackPerChunk) {
            entity.remove();
            return;
        }

        SBMob existing = findExistingStackNotFull(chunk, entity, mobCfg.maxStack);
        if (existing != null) {
            int availableSpace = mobCfg.maxStack - existing.getStackSize();
            int amountToAdd = Math.min(spawnCount, availableSpace);

            existing.incrementStack(amountToAdd);
            entity.remove();

            if (amountToAdd < spawnCount) {
                SBLogger.raw("<yellow>Mob stack reached max size (" + mobCfg.maxStack + "), only added " + amountToAdd + " of " + spawnCount + " mobs");
            }
        } else {
            int stackSize = Math.min(spawnCount, mobCfg.maxStack);
            SBMob sbMob = new SBMob(entity, stackSize);
            stackManager.getAll().put(entity.getUniqueId(), sbMob);
            if (mobCfg.disableAI && entity instanceof Mob mob) {
                mob.setAware(false);
            }

            if (stackSize < spawnCount) {
                SBLogger.raw("<yellow>Mob stack limited to max size (" + mobCfg.maxStack + "), spawned " + stackSize + " of " + spawnCount + " mobs");
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

                FeatureConfig.MobStacker mobCfg = Main.getInstance().getConfig(FeatureConfig.class).mobStacker;
                if (mobCfg.disableAI && newEntity instanceof Mob mob) {
                    mob.setAware(false);
                }

                stackManager.getAll().put(newEntity.getUniqueId(), newStack);
                stackManager.remove(entity.getUniqueId());
            }, 1L);
        } else {
            stackManager.remove(entity.getUniqueId());
        }
    }

    private SBMob findExistingStackNotFull(Chunk chunk, LivingEntity entity, int maxStack) {
        return stackManager.getAll().values().stream()
                .filter(sb -> sb instanceof SBMob sbMob
                        && sbMob.getEntity().getChunk().equals(chunk)
                        && sbMob.getEntity().getType() == entity.getType()
                        && sbMob.getStackSize() < maxStack)
                .map(sb -> (SBMob) sb)
                .findFirst()
                .orElse(null);
    }

    private int getSpawnCountFromSpawner(CreatureSpawner spawner) {
        FeatureConfig.SpawnerStacker spawnerCfg = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

        if (!spawnerCfg.enabled) {
            return 1;
        }

        var spawnerManager = Main.getSpawnerManager();
        var spawnerData = spawnerManager.get(spawner.getLocation());

        if (spawnerData.isPresent()) {
            int stackSize = spawnerData.get().getStackSize();
            int level = spawnerData.get().getLevel();

            int baseSpawnCount = 1;
            double spawnMultiplier = 1 + (stackSize - 1) * spawnerCfg.spawnMultiplierPerStack +
                    level * spawnerCfg.spawnMultiplierPerLevel;
            int spawnCount = (int) Math.round(baseSpawnCount * spawnMultiplier);

            return Math.max(1, Math.min(spawnCount, spawnerCfg.maxStack));
        }

        return 1;
    }
}