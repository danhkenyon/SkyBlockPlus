package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

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
