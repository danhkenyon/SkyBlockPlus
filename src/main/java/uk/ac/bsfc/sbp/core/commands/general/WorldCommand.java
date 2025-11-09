package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCommand extends SBCommand {
    public WorldCommand() {
        super();
        super.name("world");
        super.description("World Command");
        super.aliases("world-manager");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        if (args.length < 1) {
            player.sendMessage("{messages.commands.world.usages.global}");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create": {
                if (args.length < 2) {
                    player.sendMessage("{messages.commands.world.usages.create}");
                    return;
                }

                SBWorld.create(
                        args[1],
                        (args.length >= 3) ? args[2].toUpperCase() : "NORMAL",
                        (args.length >= 4) ? Long.parseLong(args[3]) : 1L
                );
                player.sendMessage("World '" + args[1] + "' created successfully!");
                break;
            }
            case "list": {
                List<SBWorld> worlds = SBWorldUtils.getInstance().getLoadedWorlds();
                if (worlds.isEmpty()) {
                    player.sendMessage("{messages.worlds.no-loaded-worlds}");
                } else {
                    player.sendMessage(Component.text("Loaded Worlds:").color(NamedTextColor.AQUA));
                    for (SBWorld w : worlds) {
                        String msg = "<click:run_command:/world teleport " + w.getName() + ">"
                                + "<hover:show_text:'<green>Teleport'>"
                                + "<green>" + w.getName() + "</green>"
                                + "</hover></click>";
                        player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
                    }
                }
                break;
            }
            case "teleport": {
                if (args.length < 2) {
                    player.sendMessage("Usage: /world teleport <Name>");
                    return;
                }
                String name = args[1];
                SBWorld world = SBWorldUtils.getInstance().getWorld(name);
                if (world == null) {
                    player.sendMessage("World '" + name + "' not found.");
                    return;
                }
                player.teleport(SBLocation.of(world));
                player.sendMessage("Teleported to world '" + name + "'.");
                break;
            }
            case "delete": {
                if (args.length < 2) {
                    player.sendMessage("Usage: /world delete <Name>");
                    return;
                }
                String name = args[1];
                SBWorld world = SBWorldUtils.getInstance().getWorld(name);
                if (world == null) {
                    player.sendMessage("World '" + name + "' not found.");
                    return;
                }
                boolean deleted = world.delete();
                player.sendMessage(deleted
                        ? "World '" + name + "' deleted successfully."
                        : "Failed to delete world '" + name + "'.");
                break;
            }
            default:
                player.sendMessage("{messages.commands.world.usages.global}");
        }
    }

    @Override
    public List<String> suggestions(int index) {
        return switch (index) {
            case 0 -> Arrays.asList("create", "list", "teleport", "delete");
            case 1 -> switch (args[0].toLowerCase()) {
                case "teleport", "delete" -> SBWorldUtils.getInstance().getLoadedWorlds()
                        .stream()
                        .map(SBWorld::getName)
                        .toList();
                default -> List.of();
            };
            default -> List.of();
        };
    }
}
