package uk.ac.bsfc.sbp.core.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.core.spawners.SpawnerDAO;
import uk.ac.bsfc.sbp.core.spawners.SpawnerData;
import uk.ac.bsfc.sbp.core.spawners.SpawnerGUI;
import uk.ac.bsfc.sbp.core.spawners.SpawnerStackManager;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;

import java.util.UUID;

public class SpawnerListener extends SBEventHandler {

    private final SpawnerStackManager manager;
    private final SpawnerDAO dao;

    public SpawnerListener() {
        this.manager = Main.getSpawnerManager();
        this.dao = Main.getSpawnerDAO();
    }

    @Event(async = false)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        FeatureConfig.SpawnerStacker mobCfg = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
        if (!mobCfg.enabled) return;

        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        ItemStack item = event.getItemInHand();
        EntityType itemType = getEntityTypeFromItem(item);

        EntityType spawnerType;
        if (itemType != null) {
            spawnerType = itemType;
        } else {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawnerType = spawner.getSpawnedType();
        }

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(spawnerType);
        updateSpawnerStats(spawner, 1, 1);
        spawner.update(true, false);

        if (mobCfg.autoMerge) {
            long chunkCount = manager.getAll().stream()
                    .filter(s -> s.getLocation().getChunk().equals(block.getChunk()))
                    .count();
            if (chunkCount >= mobCfg.maxSpawnerStacksPerChunk) {
                event.getPlayer().sendMessage("§cMax spawner stacks reached in this chunk!");
                return;
            }
        }

        final EntityType finalType = spawnerType;
        manager.get(block.getLocation()).ifPresentOrElse(existing -> {
            if (mobCfg.mustMatchSpawnerType && existing.getType() != finalType) {
                event.getPlayer().sendMessage("§cCannot stack different spawner types!");
                return;
            }

            if (existing.getStackSize() >= mobCfg.maxStack) {
                event.getPlayer().sendMessage("§cSpawner stack is full!");
                return;
            }

            existing.setStackSize(existing.getStackSize() + 1);
            updateSpawnerStats((CreatureSpawner) block.getState(), existing.getStackSize(), existing.getLevel());
            dao.saveSpawner(existing);

            event.getPlayer().sendMessage("§aSpawner stacked! Now size: §e" + existing.getStackSize());
        }, () -> {
            SpawnerData data = new SpawnerData(UUID.randomUUID(), block.getLocation(), finalType, 1, 1);
            manager.add(data);
            dao.saveSpawner(data);

            event.getPlayer().sendMessage("§aPlaced a new spawner!");
        });
    }

    @Event(async = false)
    public void onSpawnerBreak(BlockBreakEvent event) {
        FeatureConfig.SpawnerStacker mobCfg = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
        if (!mobCfg.enabled) return;

        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        manager.get(block.getLocation()).ifPresent(data -> {
            if (data.getType() == null) {
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                data.setType(spawner.getSpawnedType());
            }

            int size = data.getStackSize();

            if (size > 1) {
                data.setStackSize(size - 1);
                dao.saveSpawner(data);

                if (!mobCfg.dropStackedSpawner) {
                    event.setDropItems(false);
                }

                event.getPlayer().sendMessage("§cSpawner unstacked! Remaining size: §e" + (size - 1));
            } else {
                manager.remove(block.getLocation());
                dao.deleteSpawner(block.getLocation());

                event.getPlayer().sendMessage("§cSpawner removed.");
            }
        });
    }

    @Event(async = false)
    public void onSpawnerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.SPAWNER) return;

        FeatureConfig.SpawnerStacker mobCfg = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
        if (!mobCfg.enabled) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.SPAWNER && handItem.hasItemMeta()) {
            String handSpawnerType = handItem.getItemMeta().getPersistentDataContainer()
                    .get(NKeys.getKey("spawner_type"), PersistentDataType.STRING);

            manager.get(block.getLocation()).ifPresent(existing -> {
                if (existing.getType() == null) {
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    existing.setType(spawner.getSpawnedType());
                }

                if (handSpawnerType != null) {
                    EntityType handType = EntityType.valueOf(handSpawnerType);

                    if (mobCfg.mustMatchSpawnerType && existing.getType() != handType) {
                        player.sendMessage("§cCannot stack different spawner types!");
                        return;
                    }
                }

                if (existing.getStackSize() >= mobCfg.maxStack) {
                    player.sendMessage("§cSpawner stack is full!");
                    return;
                }

                if (handItem.getAmount() > 1) {
                    handItem.setAmount(handItem.getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }

                existing.setStackSize(existing.getStackSize() + 1);
                updateSpawnerStats((CreatureSpawner) block.getState(), existing.getStackSize(), existing.getLevel());
                dao.saveSpawner(existing);

                player.sendMessage("§aSpawner stacked! Now size: §e" + existing.getStackSize());
                event.setCancelled(true);
            });
        } else {
            manager.get(block.getLocation()).ifPresent(data -> {
                if (data.getType() == null) {
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    data.setType(spawner.getSpawnedType());
                    dao.saveSpawner(data);
                }
                SpawnerGUI.openSpawnerGUI(player, data, manager, dao);
                event.setCancelled(true);
            });
        }
    }

    private EntityType getEntityTypeFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        String typeString = item.getItemMeta().getPersistentDataContainer()
                .get(NKeys.getKey("spawner_type"), PersistentDataType.STRING);

        if (typeString != null) {
            try {
                return EntityType.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private void updateSpawnerStats(CreatureSpawner spawner, int stackSize, int level) {
        FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

        int baseSpawnCount = 1;
        double spawnMultiplier = 1 + (stackSize - 1) * config.spawnMultiplierPerStack + level * config.spawnMultiplierPerLevel;
        int spawnCount = (int) Math.round(baseSpawnCount * spawnMultiplier);
        spawnCount = Math.max(1, Math.min(spawnCount, 16));

        int baseSpawnDelay = 20;
        int spawnDelay = Math.max(10, baseSpawnDelay - (level * config.spawnDelayReductionPerLevel));

        try {
            org.bukkit.block.BlockState state = spawner.getBlock().getState();
            if (state instanceof CreatureSpawner) {
                CreatureSpawner cs = (CreatureSpawner) state;

                cs.setSpawnCount(spawnCount);

                cs.setDelay(spawnDelay);

                cs.update(true, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}