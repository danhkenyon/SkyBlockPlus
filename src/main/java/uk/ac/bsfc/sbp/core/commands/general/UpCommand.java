package uk.ac.bsfc.sbp.core.commands.general;

import org.bukkit.Material;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * Represents the "up" command, which allows a player to move up by a specified number of blocks.
 * The command places a glass block for the player to stand on before teleporting them upward.
 * This command is only available to players in the game.
 */
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
            user.sendMessage("<red>This command can only be used by players.");
            return;
        }
        if (args.length != 1) {
            user.sendMessage("<red>Usage: " + usage());
            return;
        }

        int blocks;
        try {
            blocks = Integer.parseInt(args[0]);
            SBLocation loc = player.location();

            player.currentWorld().toBukkit().getBlockAt(loc.toBukkit()).setType(Material.GLASS);
            player.teleport(SBLocation.of(
                    loc.getWorld(),
                    loc.getX(),
                    loc.getY() + blocks,
                    loc.getZ(),
                    loc.getYaw(),
                    loc.getPitch()
            ));
            player.sendMessage("{messages.commands.up-;success}", Placeholder.of("%blocks%", blocks));
        } catch (NumberFormatException e) {
            user.sendMessage("<red>Invalid number of blocks: " + args[0]);
        }
    }
}
