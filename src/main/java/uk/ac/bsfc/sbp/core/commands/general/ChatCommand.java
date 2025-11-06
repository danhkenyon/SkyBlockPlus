package uk.ac.bsfc.sbp.core.commands.general;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.game.SBServer;
import uk.ac.bsfc.sbp.utils.user.SBUserType;

public class ChatCommand extends SBCommand {
    public ChatCommand() {
        super();

        this.name("chat");
        this.description("Sends a chat message.");
    }

    @Override
    public void execute() {
        if (super.getUser().getUserType() == SBUserType.PLAYER || super.getUser().getUserType() == SBUserType.UNDEFINED) {
            super.getUser().sendMessage("&cOnly the console can use this command.");
            return;
        }
        if (super.args().length == 0) {
            super.getUser().sendMessage("&cUsage: /chat <message>");
            return;
        }

        String message = String.join(" ", super.args());
        SBServer.broadcastRaw("&f&lConsole: &f" + message);
    }
}
