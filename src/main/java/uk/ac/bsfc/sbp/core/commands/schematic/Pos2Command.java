package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBMath;
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
        if (!(super.getUser() instanceof SBPlayer)) {
            super.getUser().sendMessage("&cOnly players can use this command.");
            return;
        }
        Location loc = super.getUser().toBukkit(Player.class).getLocation();
        RegionUtils.getInstance().inputLoc2(super.getUser().to(SBPlayer.class), loc);

        super.getUser().sendMessage("&aSet position 2 to &o[&f&o"+
                SBMath.round(loc.x(), 1) + "&a&o, " +
                "&f&o"+SBMath.round(loc.y(), 1)+"&a&o, " +
                "&f&o"+SBMath.round(loc.z(), 1)+"&a&o]");
    }
}
