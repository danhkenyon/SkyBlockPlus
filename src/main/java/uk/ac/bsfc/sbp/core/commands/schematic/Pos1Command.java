package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * Represents a command that sets the first position for a schematic selection.
 * This command is typically used in world editing operations, allowing the user
 * to define one corner of a selection region.
 *
 * The command is executed by players to set their current location as the
 * first position (Position 1). If executed by a non-player entity or context,
 * an appropriate message is sent to indicate that the command is for player
 * use only.
 *
 * Key behavior:
 * - Sets Position 1 for the schematic selection based on the player's current
 *   location.
 * - Sends feedback messages to the user regarding the success or failure
 *   of the operation.
 *
 * This class extends from SBCommand and uses its base functionality for
 * managing command properties, such as name, description, and usage.
 */
public class Pos1Command extends SBCommand {
    public Pos1Command() {
        super();

        super.name("/pos1");
        super.description("Set the first position for a schematic selection.");
        super.usage("//pos1");
    }

    @Override
    public void execute() {
        if (!(super.getUser() instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }
        RegionUtils.getInstance().inputLoc1(player, player.location());
        player.sendMessage("{messages.world-edit.pos1-set}");
    }
}
