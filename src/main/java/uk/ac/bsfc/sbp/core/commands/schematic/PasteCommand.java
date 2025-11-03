package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.SBFiles;
import uk.ac.bsfc.sbp.utils.schematic.*;

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
        Schematic schematic = SchematicParser.load(SBFiles.get("schematics/tree.json"));
        SchematicPlacer.place(schematic, user.getWorld(), user.toBukkit().getLocation(), Rotation.NONE, Mirror.NONE);
    }
}
