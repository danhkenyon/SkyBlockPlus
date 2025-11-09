package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class Pos2Command extends SBCommand {
    public Pos2Command() {
        super();

        super.name("/pos2");
        super.description("Set the second position for a schematic selection.");
        super.usage("//pos2");
    }

    @Override
    public void execute() {
        if (!(super.getUser() instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }
        RegionUtils.getInstance().inputLoc2(player, player.location());
        player.sendMessage("{messages.world-edit.pos2-set}");
    }
}
