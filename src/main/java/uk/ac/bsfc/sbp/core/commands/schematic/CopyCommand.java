package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.SBConstants;
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
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        Region region = RegionUtils.getInstance().getRegion(player);
        if (!region.isComplete()) {
            player.sendMessage("{messages.world-edit.no_region_selected}");
            return;
        }

        player.clipboard().add(region.copy());
        player.sendMessage("{messages.world-edit.async} {messages.world-edit.clipboard-copy}");
    }
}
