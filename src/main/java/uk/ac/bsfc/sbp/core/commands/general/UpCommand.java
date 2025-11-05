package uk.ac.bsfc.sbp.core.commands.general;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class UpCommand extends SBCommand {
    public UpCommand() {
        super();

        super.name("up");
        super.description("Move up a specified number of blocks.");
        super.usage("/up <blocks>");
    }

    @Override
    public void execute() {
        if (args.length != 1) {
            user.sendMessage("Usage: " + usage());
            return;
        }

    }
}
