package uk.ac.bsfc.sbp.core.commands.general;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
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
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("&cThis command can only be used by players.");
            return;
        }
        if (args.length != 1) {
            user.sendMessage("&cUsage: " + usage());
            return;
        }

        int blocks;
        try {
            blocks = Integer.parseInt(args[0]);
            SBLocation loc = player.location();

            player.teleport(SBLocation.of(
                    loc.getWorld(),
                    loc.getX(),
                    loc.getY() + blocks,
                    loc.getZ(),
                    loc.getYaw(),
                    loc.getPitch()
            ));
        } catch (NumberFormatException e) {
            user.sendMessage("&cInvalid number of blocks: " + args[0]);
        }
    }
}
