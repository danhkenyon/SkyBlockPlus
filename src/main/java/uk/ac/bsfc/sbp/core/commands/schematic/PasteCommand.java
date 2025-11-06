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
        if (!(super.getUser() instanceof SBPlayer player)) {
            user.sendMessage("&cOnly players can use this command.");
            return;
        }

        if (super.args().length == 0) {
            BlockSet schematic = user.to(SBPlayer.class).clipboard().getLast();
            if (schematic == null) {
                user.sendMessage("&cFailed to paste.");
                return;
            }
            schematic.paste(player.location());

            int blocks = -1;
            if (schematic instanceof Schematic) {
                blocks = ((Schematic) schematic).blocks().size();
            }else if (schematic instanceof Region) {
                blocks = ((Region) schematic).copy().blocks().size();
            }

            String prefix = SBConstants.Schematics.ASYNC ? "&8[&4&lASYNC&8] " : "";
            user.sendMessage("{messages.async} &7Clipboard pasted at "+player.location().format()+". ("+blocks+" blocks)");
        }
    }
}
