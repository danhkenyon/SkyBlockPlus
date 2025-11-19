package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.Region;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * The CopyCommand class represents a command within an application for the purpose of copying
 * a defined region to a clipboard at the player's current location.
 * <br>
 * This class extends the {@code SBCommand} class and provides specific behavior and functionality
 * for the copy operation, particularly for players interacting with schematic regions.
 *
 * Features:
 * - The command is named "/copy".
 * - Described as "Paste a schematic at your location."
 * - Provides usage instructions as "/copy".
 * <br>
 * Behavior:
 * - Ensures the command is executed by a player and not other types of users.
 * - Uses a defined region selected by the player for copying.
 * - Validates that the region is complete before proceeding.
 * - Adds the copied region to the player's clipboard.
 * - Notifies the player with appropriate messages about the operation's success or issues.
 *
 * This command integrates with region utilities and clipboard functionality to support efficient
 * manipulation of game data, enhancing the user experience when managing schematics.
 */
public class CopyCommand extends SBCommand {
    public CopyCommand() {
        super();

        super.name("/copy");
        super.description("Paste a schematic at your location.");
        super.usage("/copy");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        Region region = RegionUtils.getInstance().getRegion(player);
        if (region.isComplete()) {
            player.sendMessage("{messages.world-edit.no_region_selected}");
            return;
        }

        player.clipboard().add(region.copy());
        player.sendMessage("{messages.world-edit.async} {messages.world-edit.clipboard-copy}");
    }
}
