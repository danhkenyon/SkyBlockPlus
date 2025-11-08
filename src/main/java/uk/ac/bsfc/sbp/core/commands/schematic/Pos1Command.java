package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBMath;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class Pos1Command extends SBCommand {
    public Pos1Command() {
        super();

        super.name("/pos1");
        super.description("Set the first position for a schematic selection.");
        super.usage("//pos1");
    }

    @Override
    public void execute() {
        if (!(super.getUser() instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }
        RegionUtils.getInstance().inputLoc1(player, player.location());
        player.sendMessage("{messages.world-edit.pos1-set}");
    }
}
