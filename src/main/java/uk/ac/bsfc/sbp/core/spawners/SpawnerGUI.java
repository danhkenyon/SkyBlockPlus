package uk.ac.bsfc.sbp.core.spawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpawnerGUI {

    private static final Map<Player, SpawnerData> playerDataMap = new HashMap<>();

    public static void openSpawnerGUI(Player player, SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
        playerDataMap.put(player, data);
        openSpawnerGUIRefresh(player, data, manager, dao);
    }

    private static void openSpawnerGUIRefresh(Player player, SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
        FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# . i . u . d . #",
                        "# . . p . . . . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ")))
                .addIngredient('i', new InfoItem(data))
                .addIngredient('u', new UpgradeItem(data, manager, dao, player))
                .addIngredient('d', new DowngradeItem(data, manager, dao, player))
                .addIngredient('p', new PickupItem(data, manager, dao, player))
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Spawner Management - " + data.getType().name())
                .setGui(gui)
                .build();

        window.open();
    }

    private static void refreshGUI(Player player, SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
        player.closeInventory();
        Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            openSpawnerGUIRefresh(player, data, manager, dao);
        }, 2L);
    }

    private static class InfoItem extends AbstractItem {
        private final SpawnerData data;

        public InfoItem(SpawnerData data) {
            this.data = data;
        }

        @Override
        public ItemProvider getItemProvider() {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

            ItemStack item = new ItemStack(Material.SPAWNER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§eSpawner Info");
            meta.setLore(Arrays.asList(
                    "§7Type: §f" + data.getType().name(),
                    "§7Stack Size: §f" + data.getStackSize(),
                    "§7Level: §f" + data.getLevel(),
                    "",
                    "§7Spawn Multiplier: §f" + String.format("%.2f", (1 + (data.getStackSize() - 1) * config.spawnMultiplierPerStack + data.getLevel() * config.spawnMultiplierPerLevel)) + "x",
                    "§7Spawn Delay: §f" + Math.max(10, 20 - data.getLevel() * config.spawnDelayReductionPerLevel) + " ticks"
            ));
            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            //444
        }
    }

    private static class UpgradeItem extends AbstractItem {
        private final SpawnerData data;
        private final SpawnerStackManager manager;
        private final SpawnerDAO dao;
        private final Player player;

        public UpgradeItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao, Player player) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
            this.player = player;
        }

        @Override
        public ItemProvider getItemProvider() {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
            boolean canUpgrade = data.getLevel() < config.maxLevel;

            ItemStack item = new ItemStack(canUpgrade ? Material.EXPERIENCE_BOTTLE : Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(canUpgrade ? "§aUpgrade Spawner" : "§cMax Level Reached");

            if (canUpgrade) {
                int cost = config.baseUpgradeCost * (data.getLevel() + 1);
                meta.setLore(Arrays.asList(
                        "§7Current Level: §f" + data.getLevel(),
                        "§7Next Level: §f" + (data.getLevel() + 1),
                        "§7Cost: §6" + cost + " XP",
                        "",
                        "§eClick to upgrade!"
                ));
            } else {
                meta.setLore(Arrays.asList(
                        "§7Current Level: §f" + data.getLevel(),
                        "§7Max Level: §f" + config.maxLevel,
                        "",
                        "§cCannot upgrade further!"
                ));
            }
            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

            if (data.getLevel() >= config.maxLevel) {
                player.sendMessage("§cThis spawner is already at maximum level!");
                return;
            }

            int cost = config.baseUpgradeCost * (data.getLevel() + 1);
            if (player.getLevel() < cost) {
                player.sendMessage("§cYou need " + cost + " XP levels to upgrade this spawner!");
                return;
            }

            player.setLevel(player.getLevel() - cost);
            data.setLevel(data.getLevel() + 1);
            dao.saveSpawner(data);

            player.sendMessage("§aSpawner upgraded to level " + data.getLevel() + "!");
            refreshGUI(player, data, manager, dao);
        }
    }

    private static class DowngradeItem extends AbstractItem {
        private final SpawnerData data;
        private final SpawnerStackManager manager;
        private final SpawnerDAO dao;
        private final Player player;

        public DowngradeItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao, Player player) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
            this.player = player;
        }

        @Override
        public ItemProvider getItemProvider() {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
            boolean canDowngrade = data.getLevel() > 1;

            ItemStack item = new ItemStack(canDowngrade ? Material.GLASS_BOTTLE : Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(canDowngrade ? "§6Downgrade Spawner" : "§cMinimum Level");

            if (canDowngrade) {
                int refund = config.baseUpgradeCost * data.getLevel() / 2;
                meta.setLore(Arrays.asList(
                        "§7Current Level: §f" + data.getLevel(),
                        "§7Next Level: §f" + (data.getLevel() - 1),
                        "§7Refund: §6" + refund + " XP",
                        "",
                        "§eClick to downgrade!"
                ));
            } else {
                meta.setLore(Arrays.asList(
                        "§7Current Level: §f" + data.getLevel(),
                        "§7Minimum Level: §f1",
                        "",
                        "§cCannot downgrade further!"
                ));
            }
            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

            if (data.getLevel() <= 1) {
                player.sendMessage("§cThis spawner is already at minimum level!");
                return;
            }

            int refund = config.baseUpgradeCost * data.getLevel() / 2;
            player.setLevel(player.getLevel() + refund);
            data.setLevel(data.getLevel() - 1);
            dao.saveSpawner(data);

            player.sendMessage("§6Spawner downgraded to level " + data.getLevel() + " and refunded " + refund + " XP!");
            refreshGUI(player, data, manager, dao);
        }
    }

    private static class PickupItem extends AbstractItem {
        private final SpawnerData data;
        private final SpawnerStackManager manager;
        private final SpawnerDAO dao;
        private final Player player;

        public PickupItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao, Player player) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
            this.player = player;
        }

        @Override
        public ItemProvider getItemProvider() {
            ItemStack item = new ItemStack(Material.CHEST_MINECART);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6Pickup Spawner Stack");
            meta.setLore(Arrays.asList(
                    "§7Stack Size: §f" + data.getStackSize(),
                    "§7Level: §f" + data.getLevel(),
                    "",
                    "§eClick to pickup the entire stack!",
                    "§cWarning: This will remove the spawner from the world!"
            ));
            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;

            ItemStack spawnerItem = createSpawnerItem(data.getType(), data.getStackSize(), data.getLevel());

            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(spawnerItem);
            if (!leftover.isEmpty()) {
                player.sendMessage("§cNot enough inventory space to pickup the spawner stack!");
                return;
            }

            Block block = data.getLocation().getBlock();
            if (block.getType() == Material.SPAWNER) {
                block.setType(Material.AIR, false);
            }

            manager.remove(data.getLocation());
            dao.deleteSpawner(data.getLocation());

            player.sendMessage("§aPicked up spawner stack! (" + data.getStackSize() + " spawners, Level " + data.getLevel() + ")");
            player.closeInventory();
        }

        private ItemStack createSpawnerItem(org.bukkit.entity.EntityType type, int stackSize, int level) {
            ItemStack spawner = new ItemStack(Material.SPAWNER, stackSize);
            ItemMeta meta = spawner.getItemMeta();
            meta.setDisplayName("§e" + type.name() + " Spawner §7(Level " + level + ")");
            meta.setLore(Arrays.asList(
                    "§7Type: §f" + type.name(),
                    "§7Stack Size: §f" + stackSize,
                    "§7Level: §f" + level,
                    "",
                    "§aRight-click to place"
            ));

            meta.getPersistentDataContainer().set(
                    NKeys.getKey("spawner_type"),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    type.name()
            );
            meta.getPersistentDataContainer().set(
                    NKeys.getKey("spawner_level"),
                    org.bukkit.persistence.PersistentDataType.INTEGER,
                    level
            );

            spawner.setItemMeta(meta);
            return spawner;
        }
    }
}