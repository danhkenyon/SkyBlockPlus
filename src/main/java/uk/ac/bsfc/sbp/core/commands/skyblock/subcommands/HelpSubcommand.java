package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class HelpSubcommand {
    public static void execute(SBCommand cmd) {
        SBUser user = cmd.getUser();

        user.sendMessage("");
        user.sendMessage("<gold><b>=== <yellow><b>SkyBlock Help <gold><b>===");
        user.sendMessage(
                "<yellow><b>/island",
                "<gray>- <aqua>Island creation help."
        );
        user.sendMessage(
                "<yellow><b>/island help",
                "<gray>- <aqua>Displays this help message."
        );
        user.sendMessage(
                "<yellow><b>/island create [island_name]",
                "<gray>- <aqua>Creates a new island."
        );
        user.sendMessage(
                "<yellow><b>/island invite <name> [rank]",
                "<gray>- <aqua>Adds a user to the island's team."
        );
        user.sendMessage(
                "<yellow><b>/island invite <accept | deny> <island>",
                "<gray>- <aqua>Allows a user to accept or decline an invite."
        );
        user.sendMessage(
                "<yellow><b>/island kick <name>",
                "<gray>- <aqua>Removes a user from the island's team."
        );
        user.sendMessage(
                "<yellow><b>/island spawn",
                "<gray>- <aqua>Teleports the user to the island's spawn location."
        );
        user.sendMessage(
                "<yellow><b>/island setspawn",
                "<gray>- <aqua>Changes the island's spawn location to the user's location."
        );
        user.sendMessage("");
    }
}
