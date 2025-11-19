package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBConsole;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnerGiveCommand extends SBCommand {
    public SpawnerGiveCommand() {
        super();

        super.name("spawnergive");
        super.description("Give spawners to players.");
        super.usage("/spawnergive <player> <type> [amount] [level]");
        super.permission("sbp.spawnergive");

        super.aliases("sgive", "givespawner");
    }

    @Override
    public void execute() {
        if (super.getUser() instanceof SBConsole) {
            SBLogger.err("Command cannot be ran by CONSOLE.");
            return;
        }

        if (args.length < 2) {
            user.sendMessage("<red>Usage: /spawnergive <player> <type> [amount] [level]");
            user.sendMessage("<yellow>Available types: " + getEntityTypes());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            user.sendMessage("<red>Player not found: " + args[0]);
            return;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            user.sendMessage("<red>Invalid entity type: " + args[1]);
            user.sendMessage("<yellow>Available types: " + getEntityTypes());
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    user.sendMessage("<red>Amount must be between 1 and 64");
                    return;
                }
            } catch (NumberFormatException e) {
                user.sendMessage("<red>Invalid amount: " + args[2]);
                return;
            }
        }

        int level = 1;
        if (args.length >= 4) {
            try {
                level = Integer.parseInt(args[3]);
                if (level < 1 || level > 5) {
                    user.sendMessage("<red>Level must be between 1 and 5");
                    return;
                }
            } catch (NumberFormatException e) {
                user.sendMessage("<red>Invalid level: " + args[3]);
                return;
            }
        }

        ItemStack spawner = createSpawnerItem(entityType, amount, level);
        target.getInventory().addItem(spawner);

        user.sendMessage("<green>Given " + amount + " " + entityType.name() + " spawner(s) (Level " + level + ") to " + target.getName());
        target.sendMessage("<green>You received " + amount + " " + entityType.name() + " spawner(s) (Level " + level + ")");
    }

    @Override
    public List<String> suggestions(int index) {
        return switch(index) {
            case 0 -> Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            case 1 -> Arrays.stream(EntityType.values())
                    .filter(EntityType::isSpawnable)
                    .filter(EntityType::isAlive)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            case 2 -> List.of("1", "8", "16", "32", "64");
            case 3 -> List.of("1", "2", "3", "4", "5");
            default -> List.of();
        };
    }

    private ItemStack createSpawnerItem(EntityType type, int amount, int level) {
        ItemStack spawner = new ItemStack(Material.SPAWNER, amount);
        ItemMeta meta = spawner.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize("<yellow>" + type.name() + " Spawner <gray>(Level " + level + ")"));
        meta.lore(Arrays.asList(
                MiniMessage.miniMessage().deserialize("<gray>Type: <white>" + type.name()),
                MiniMessage.miniMessage().deserialize("<gray>Stack Size: <white>" + amount),
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

    private String getEntityTypes() {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .filter(EntityType::isAlive)
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
    }
}
