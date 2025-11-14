package uk.ac.bsfc.sbp.core.commands.general;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * The FlySpeedCommand class is a command used to modify a player's fly speed.
 * This command is intended to be executed by players only and allows players to set their fly speed
 * to a value between 1 and 10.
 *
 * Features:
 * - Validates the arguments to ensure that the input is a valid number between 1 and 10.
 * - Provides feedback to the player when they successfully change their fly speed or if they
 *   encounter any errors during execution.
 * - Automatically adjusts the input value to the corresponding fly speed multiplier for the game.
 */
public class FlySpeedCommand extends SBCommand {
    public FlySpeedCommand() {
        super();

        this.name("flyspeed");
        this.description("Changes fly speed.");
        this.usage("/flyspeed <1-10>");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("<red>This command can only be used by players.");
            return;
        }
        if (args.length != 1) {
            user.sendMessage("<red>Usage: " + usage());
            return;
        }

        int i;

        try {
            i = Integer.parseInt(args[0]);
            if (i < 1 || i > 10) {
                player.sendMessage("<red>Value must be between 1 and 10!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("<red>Please enter a valid number between 1 and 10!");
            return;
        }

        float realNum = i / 10f;
        player.setFlySpeed(realNum);
        player.sendMessage("<green>Fly speed set to <gold>" + i + "<green>!");


    }
}
