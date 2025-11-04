package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.SBFiles;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.io.FileWriter;
import java.io.IOException;

public class PasteCommand extends SBCommand {
    public PasteCommand() {
        super();

        super.name("/paste");
        super.description("Paste a schematic at your location.");
        super.usage("/paste");
    }

    @Override
    public void execute() {
        // TODO: impl
        if (!(super.getUser() instanceof SBPlayer)) {
            super.getUser().sendMessage("&cOnly players can use this command.");
            return;
        }

        if (super.args().length == 0) {
            BlockSet schematic = user.to(SBPlayer.class).clipboard().getLast();
            if (schematic == null) {
                user.sendMessage("&cFailed to paste schematic.");
                return;
            }

            schematic.paste(user.toBukkit(Player.class).getLocation());
            user.sendMessage("&aPasted schematic from clipboard.");
        }
    }
}
