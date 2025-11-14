package uk.ac.bsfc.sbp.core.commands.general;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.game.SBServer;
import uk.ac.bsfc.sbp.utils.user.SBUserType;

/**
 * Represents a command that allows the console to broadcast a chat message to all players.
 * The `ChatCommand` class extends the abstract `SBCommand` class, providing
 * the functionality of sending messages to all players via the console.
 *
 * This command checks the type of the user attempting to execute it. Only users
 * of the type `CONSOLE` are permitted to use this command. If a user of type
 * `PLAYER` or `UNDEFINED` tries to execute it, an error message is sent to the user,
 * and the command terminates.
 *
 * If the command arguments are empty, it informs the user about correct usage.
 *
 * Otherwise, the command constructs a concatenated message from the arguments
 * and broadcasts it, prefixed with "Console:".
 *
 * Override Methods:
 * - `execute`: Handles the execution logic for the chat command. It validates
 *   the user type, processes the input arguments, and sends a formatted broadcast
 *   message to all players.
 */
public class ChatCommand extends SBCommand {
    public ChatCommand() {
        super();

        this.name("chat");
        this.description("Sends a chat message.");
    }

    @Override
    public void execute() {
        if (super.getUser().getUserType() == SBUserType.PLAYER || super.getUser().getUserType() == SBUserType.UNDEFINED) {
            super.getUser().sendMessage("<red>Only the console can use this command.");
            return;
        }
        if (super.args().length == 0) {
            super.getUser().sendMessage("<red>Usage: /chat <message>");
            return;
        }

        String message = String.join(" ", super.args());
        SBServer.broadcastRaw("<white><b>Console: <white>" + message);
    }
}
