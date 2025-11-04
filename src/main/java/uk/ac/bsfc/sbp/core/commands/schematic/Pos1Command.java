package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBMath;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
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
        if (!(super.getUser() instanceof SBPlayer)) {
            super.getUser().sendMessage("&cOnly players can use this command.");
            return;
        }
        Location loc = super.getUser().toBukkit(Player.class).getLocation();
        RegionUtils.getInstance().inputLoc1(super.getUser().to(SBPlayer.class), loc);

        super.getUser().sendMessage("&aSet position 1 to &o[&f&o"+
                SBMath.round(loc.x(), 1) + "&a&o, " +
                "&f&o"+SBMath.round(loc.y(), 1)+"&a&o, " +
                "&f&o"+SBMath.round(loc.z(), 1)+"&a&o]");
    }
}
