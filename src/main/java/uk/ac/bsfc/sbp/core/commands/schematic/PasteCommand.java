package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;

public class PasteCommand extends SBCommand {
    public PasteCommand() {
        super();

        super.name("/paste");
        super.description("Paste a schematic at your location.");
        super.usage("/paste <name>");
    }

    @Override
    public void execute() {
        // TODO: impl
    }
}
