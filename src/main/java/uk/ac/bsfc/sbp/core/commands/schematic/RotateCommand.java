package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * Represents a command that allows players to rotate the schematic in their
 * clipboard by a specified angle and optionally apply a mirroring transformation.
 * This command is intended to be used by players and operates on the player's
 * clipboard, which must contain a schematic or region to function correctly.
 *
 * The command requires a single argument: the rotation angle,
 * and optionally a second argument for specifying a mirroring type.
 * If the arguments are invalid or usage is incorrect, the player will
 * be provided with appropriate error messages.
 *
 * Supported rotation angles:
 * - 90, -270: CLOCKWISE_90
 * - 180, -180: CLOCKWISE_180
 * - 270, -90: CLOCKWISE_270
 * - 0, 360, -360: NONE
 *
 * Supported mirror transformations:
 * - "lr", "left-right", "leftright": LEFT_RIGHT
 * - "fb", "front-back", "frontback": FRONT_BACK
 * - "none": NONE
 *
 * Error messages will be sent if:
 * - The command is executed by a non-player entity.
 * - The clipboard does not contain a schematic or supported region.
 * - The arguments provided are invalid or missing.
 *
 * Upon successful execution, the transformed schematic will be added to the
 * player's clipboard and a success message will be relayed to the player.
 */
public class RotateCommand extends SBCommand {
    public RotateCommand() {
        super();

        super.name("/rotate");
        super.description("Rotate the schematic in your clipboard.");
        super.usage("/rotate <angle>");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        if (super.args().length != 1) {
            player.sendMessage("<red>Usage: " + super.usage());
            return;
        }
        Rotation rot = switch (args()[0]) {
            case "90", "-270" -> Rotation.CLOCKWISE_90;
            case "180", "-180" -> Rotation.CLOCKWISE_180;
            case "270", "-90" -> Rotation.CLOCKWISE_270;
            case "0", "360", "-360" -> Rotation.NONE;
            default -> {
                player.sendMessage("<red>Usage: " + super.usage());
                yield null;
            }
        };

        Mirror mirror = Mirror.NONE;
        if (super.args().length == 2) {
            mirror = switch (args()[1].toLowerCase()) {
                case "lr", "left-right", "leftright" -> Mirror.LEFT_RIGHT;
                case "fb", "front-back", "frontback" -> Mirror.FRONT_BACK;
                case "none" -> Mirror.NONE;
                default -> {
                    player.sendMessage("<red>Unknown mirror type. Use <yellow>left-right<red> or <yellow>front-back<red>.");
                    yield null;
                }
            };
        }

        Clipboard cb = player.clipboard();
        BlockSet blockSet = cb.getLast();

        Schematic schematic;
        if (blockSet instanceof Schematic) {
            schematic = (Schematic) blockSet;
        } else if (blockSet instanceof Region region) {
            schematic = region.copy();
        } else {
            player.sendMessage("{messages.world-edit.rotation-err}");
            return;
        }

        if (schematic == null || rot == null || mirror == null) {
            return;
        }

        Schematic newSchematic = schematic.transform(rot, mirror);
        cb.add(newSchematic);
        player.sendMessage("{messages.world-edit.async} {messages.world-edit.rotation-success}", Placeholder.of("%angle%", args()[0]));
    }
}
