package uk.ac.bsfc.sbp.core.commands.schematic;

import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.SBFiles;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.List;

/**
 * The SchematicCommand class extends SBCommand and provides functionality
 * for handling schematic-related commands in the application.
 *
 * This command has two subcommands: "save" and "load".
 *
 * Usage of the command:
 * /schematic <save | load> [name]
 *
 * Additional aliases:
 * /schem
 *
 * The command is primarily targeted at players and will not execute
 * for other users, ensuring appropriate messaging when invoked in
 * non-player contexts.
 *
 * Key behaviors include:
 * - Saving schematics either with a given name or default name.
 * - Loading schematics by provided name, incorporating JSON file validation.
 * - Throwing usage messages when improper arguments or contexts are detected.
 */
public class SchematicCommand extends SBCommand {
	public SchematicCommand() {
        super();

        super.name("/schematic");
        super.description("Schematic related commands.");
        super.usage("/schematic <save | load> [name]");
        super.aliases("/schem");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }

        if (super.args().length == 0) {
            player.sendMessage("<red>Usage: " + super.usage());
            return;
        }
        String subcommand = super.args()[0].toLowerCase();
        switch (subcommand) {
            case "save" -> {
                if (!(args.length == 1 || args.length == 2)) {
                    player.sendMessage("<red>Usage: " + super.usage());
                    return;
                }

                SchematicParser.asyncSave(
                        user,
                        RegionUtils.getInstance().getRegion(player),
                        args.length == 2 ? args[1] : null
                );
            }
            case "load" -> {
                if (args.length != 2) {
                    player.sendMessage("<red>Usage: " + super.usage());
                    return;
                }

                String fileName = args[1].endsWith(".json") ? args[1] : args[1] + ".json";
                Schematic schematic = SchematicParser.asyncLoad(
                        user,
                        SBFiles.get(SBConstants.Schematics.SCHEMATIC_FOLDER + fileName)
                );
                if (schematic == null) {
                    player.sendMessage("{messages.world-edit.schem-load-err} <i>" + args[1]);
                    return;
                }
                Clipboard clipboard = ClipboardUtils.getInstance().getClipboard(player);

                clipboard.add(schematic);
                ClipboardUtils.getInstance().setClipboard(player, clipboard);
            }
        }
    }

    @Override
    public List<String> suggestions(int index) {
        return switch (index) {
            case 0 -> List.of("save", "load");
            default -> List.of();
        };
    }
}
