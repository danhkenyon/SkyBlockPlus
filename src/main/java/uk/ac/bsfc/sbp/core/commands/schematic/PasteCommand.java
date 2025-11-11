package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.BlockSet;
import uk.ac.bsfc.sbp.utils.schematic.Region;
import uk.ac.bsfc.sbp.utils.schematic.Schematic;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * The PasteCommand class represents a command to paste a schematic or region at the player's current location.
 * This command is designed to be used within a Minecraft-like environment where players can interact with
 * schematics and regions.
 *
 * The command name is "/paste". Its main function is to paste the last stored schematic or region
 * from the player's clipboard to the player's current location. It also provides feedback about
 * the operation, including the number of pasted blocks.
 *
 * Features of the command:
 * - Validates that the user executing the command is a player (not a non-player entity or console).
 * - Checks if the player's clipboard contains a schematic or region before pasting.
 * - Pasts the schematic or region at the player's location.
 * - Notifies the player about the success or failure of the operation.
 *
 * Note: The command relies on a specific user architecture that distinguishes between players
 * and other users (e.g., console). It also assumes the existence of a clipboard system tied
 * to the player.
 */
public class PasteCommand extends SBCommand {
    public PasteCommand() {
        super();

        super.name("/paste");
        super.description("Paste a schematic at your location.");
        super.usage("/paste");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        if (super.args().length == 0) {
            BlockSet schematic = player.clipboard().getLast();
            if (schematic == null) {
                player.sendMessage("{messages.world-edit.no-region-selected}");
                return;
            }

            schematic.paste(player.location());
            int blocks = (schematic instanceof Schematic) ? ((Schematic) schematic).blocks().size() : ((Region) schematic).copy().blocks().size();
            player.sendMessage("{messages.world-edit.async} {messages.world-edit.clipboard-paste} ("+blocks+" blocks)");
        }
    }
}
