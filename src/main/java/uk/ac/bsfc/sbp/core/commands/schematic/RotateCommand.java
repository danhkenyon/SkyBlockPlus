package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

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
