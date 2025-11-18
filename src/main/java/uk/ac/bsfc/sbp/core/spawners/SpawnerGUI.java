package uk.ac.bsfc.sbp.core.spawners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
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
                .addIngredient('u', new UpgradeItem(data, manager, dao))
                .addIngredient('d', new DowngradeItem(data, manager, dao))
                .addIngredient('p', new PickupItem(data, manager, dao))
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

            meta.displayName(MiniMessage.miniMessage().deserialize("<yellow>Spawner Info"));
            meta.lore(Arrays.asList(
                    MiniMessage.miniMessage().deserialize("<gray>Type: <white>" + data.getType().name()),
                    MiniMessage.miniMessage().deserialize("<gray>Stack Size: <white>" + data.getStackSize()),
                    MiniMessage.miniMessage().deserialize("<gray>Level: <white>" + data.getLevel()),
                    MiniMessage.miniMessage().deserialize(""),
                    MiniMessage.miniMessage().deserialize("<gray>Spawn Multiplier: <white>" + String.format("%.2f", (1 + (data.getStackSize() - 1) * config.spawnMultiplierPerStack + data.getLevel() * config.spawnMultiplierPerLevel)) + "x"),
                    MiniMessage.miniMessage().deserialize("<gray>Spawn Delay: <white>" + Math.max(10, 20 - data.getLevel() * config.spawnDelayReductionPerLevel) + " ticks")
            ));

            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }


        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            //444
        }
    }

    private static class UpgradeItem extends AbstractItem {
        private final SpawnerData data;
        private final SpawnerStackManager manager;
        private final SpawnerDAO dao;

        public UpgradeItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
        }

        @Override
        public ItemProvider getItemProvider() {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
            boolean canUpgrade = data.getLevel() < config.maxLevel;

            ItemStack item = new ItemStack(canUpgrade ? Material.EXPERIENCE_BOTTLE : Material.BARRIER);
            ItemMeta meta = item.getItemMeta();

            meta.displayName(MiniMessage.miniMessage().deserialize(
                    canUpgrade ? "<green>Upgrade Spawner" : "<red>Max Level Reached"
            ));

            if (canUpgrade) {
                int cost = config.baseUpgradeCost * (data.getLevel() + 1);
                meta.lore(Arrays.asList(
                        MiniMessage.miniMessage().deserialize("<gray>Current Level: <white>" + data.getLevel()),
                        MiniMessage.miniMessage().deserialize("<gray>Next Level: <white>" + (data.getLevel() + 1)),
                        MiniMessage.miniMessage().deserialize("<gray>Cost: <gold>" + cost + " XP"),
                        MiniMessage.miniMessage().deserialize(""),
                        MiniMessage.miniMessage().deserialize("<yellow>Click to upgrade!")
                ));
            } else {
                meta.lore(Arrays.asList(
                        MiniMessage.miniMessage().deserialize("<gray>Current Level: <white>" + data.getLevel()),
                        MiniMessage.miniMessage().deserialize("<gray>Max Level: <white>" + config.maxLevel),
                        MiniMessage.miniMessage().deserialize(""),
                        MiniMessage.miniMessage().deserialize("<red>Cannot upgrade further!")
                ));
            }

            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }


        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
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

        public DowngradeItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
        }

        @Override
        public ItemProvider getItemProvider() {
            FeatureConfig.SpawnerStacker config = Main.getInstance().getConfig(FeatureConfig.class).spawnerStacker;
            boolean canDowngrade = data.getLevel() > 1;

            ItemStack item = new ItemStack(canDowngrade ? Material.GLASS_BOTTLE : Material.BARRIER);
            ItemMeta meta = item.getItemMeta();

            meta.displayName(MiniMessage.miniMessage().deserialize(
                    canDowngrade ? "<gold>Downgrade Spawner" : "<red>Minimum Level"
            ));

            if (canDowngrade) {
                int refund = config.baseUpgradeCost * data.getLevel() / 2;
                meta.lore(Arrays.asList(
                        MiniMessage.miniMessage().deserialize("<gray>Current Level: <white>" + data.getLevel()),
                        MiniMessage.miniMessage().deserialize("<gray>Next Level: <white>" + (data.getLevel() - 1)),
                        MiniMessage.miniMessage().deserialize("<gray>Refund: <gold>" + refund + " XP"),
                        MiniMessage.miniMessage().deserialize(""),
                        MiniMessage.miniMessage().deserialize("<yellow>Click to downgrade!")
                ));
            } else {
                meta.lore(Arrays.asList(
                        MiniMessage.miniMessage().deserialize("<gray>Current Level: <white>" + data.getLevel()),
                        MiniMessage.miniMessage().deserialize("<gray>Minimum Level: <white>1"),
                        MiniMessage.miniMessage().deserialize(""),
                        MiniMessage.miniMessage().deserialize("<red>Cannot downgrade further!")
                ));
            }

            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }


        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
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

        public PickupItem(SpawnerData data, SpawnerStackManager manager, SpawnerDAO dao) {
            this.data = data;
            this.manager = manager;
            this.dao = dao;
        }

        @Override
        public ItemProvider getItemProvider() {
            ItemStack item = new ItemStack(Material.CHEST_MINECART);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Pickup Spawner Stack"));
            meta.lore(Arrays.asList(
                    MiniMessage.miniMessage().deserialize("<gray>Stack Size: <white>" + data.getStackSize()),
                    MiniMessage.miniMessage().deserialize("<gray>Level: <white>" + data.getLevel()),
                    MiniMessage.miniMessage().deserialize(""),
                    MiniMessage.miniMessage().deserialize("<yellow>Click to pickup the entire stack!"),
                    MiniMessage.miniMessage().deserialize("<red>Warning: This will remove the spawner from the world!"
            )));
            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, Player player, @NotNull InventoryClickEvent event) {
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
            meta.displayName(MiniMessage.miniMessage().deserialize("<yellow>" + type.name() + " Spawner <gray>(Level " + level + ")"));
            meta.lore(Arrays.asList(
                    MiniMessage.miniMessage().deserialize("<gray>Type: <white>" + type.name()),
                    MiniMessage.miniMessage().deserialize("<gray>Stack Size: <white>" + stackSize),
                    MiniMessage.miniMessage().deserialize("<gray>Level: <white>" + level),
                    MiniMessage.miniMessage().deserialize(""),
                    MiniMessage.miniMessage().deserialize("<green>Right-click to place"
                    )));

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