package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * Represents a command to set the second position for a schematic selection.
 * This command is used as part of schematic creation or editing, typically in tools that support
 * region-based operations within a world-editing environment.
 *
 * The second position will be marked at the current player's location,
 * allowing for selection of a specific rectangular region in the world.
 *
 * Inherits from the {@code SBCommand} class and overrides the {@code execute} method
 * to implement the specific behavior for this command.
 */
public class Pos2Command extends SBCommand {
    public Pos2Command() {
        super();

        super.name("/pos2");
        super.description("Set the second position for a schematic selection.");
        super.usage("//pos2");
    }

    @Override
    public void execute() {
        if (!(super.getUser() instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }
        RegionUtils.getInstance().inputLoc2(player, player.location());
        player.sendMessage("{messages.world-edit.pos2-set}");
    }
}
