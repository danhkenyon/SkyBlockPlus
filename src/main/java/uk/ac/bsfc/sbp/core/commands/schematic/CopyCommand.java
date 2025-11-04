package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.Region;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.schematic.Schematic;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class CopyCommand extends SBCommand {
    public CopyCommand() {
        super();

        super.name("/copy");
        super.description("Paste a schematic at your location.");
        super.usage("/copy");
    }

    @Override
    public void execute() {
        // TODO: impl
        if (!(super.getUser() instanceof SBPlayer)) {
            super.getUser().sendMessage("&cOnly players can use this command.");
            return;
        }

        Region region = RegionUtils.getInstance().getRegion(user.to(SBPlayer.class));
        Schematic regionSchem = region.copy();
        this.user.to(SBPlayer.class).clipboard().add(regionSchem);
        this.user.sendMessage("&aCopied region to clipboard.");
    }
}
