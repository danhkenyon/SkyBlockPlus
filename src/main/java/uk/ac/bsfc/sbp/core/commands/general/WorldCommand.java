package uk.ac.bsfc.sbp.core.commands.general;

import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.List;

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
            player.sendMessage("Usage: /world <create|list|delete> [name] [env] [seed]");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create": {
                if (args.length < 2) {
                    player.sendMessage("Usage: /world create <Name> [Normal|Nether|End] [Seed]");
                    return;
                }
                String env = "NORMAL";
                if (args.length >= 3) {
                    env = args[2].toUpperCase();
                }

                long seed = 1L;
                if (args.length >= 4) {
                    try {
                        seed = Long.parseLong(args[3]);
                    } catch (NumberFormatException ignored) {}
                }

                SBWorld world = SBWorld.create(args[1], env, seed);
                System.out.println(world);
                break;
            }


            case "list": {
                List<SBWorld> worlds = SBWorldUtils.getInstance().getLoadedWorlds();
                if (worlds.isEmpty()) {
                    player.sendMessage("No worlds loaded.");
                } else {
                    player.sendMessage("Loaded Worlds:");
                    for (SBWorld w : worlds) {
                        player.sendMessage("- " + w.getName() + " (loaded: " + w.isLoaded() + ")");
                    }
                }
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
                player.sendMessage("Unknown subcommand. Usage: /world <create|list|delete>");
        }
    }
}
