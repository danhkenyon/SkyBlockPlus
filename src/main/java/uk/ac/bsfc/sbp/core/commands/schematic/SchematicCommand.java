package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.SBFiles;
import uk.ac.bsfc.sbp.utils.schematic.*;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.List;

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
        if (!(super.getUser() instanceof SBPlayer)) {
            super.getUser().sendMessage("&cOnly players can use this command.");
            return;
        }

        if (super.args().length == 0) {
            super.getUser().sendMessage("&cUsage: " + super.usage());
            return;
        }
        String subcommand = super.args()[0].toLowerCase();
        switch (subcommand) {
            case "save" -> {
                if (!(args.length == 1 || args.length == 2)) {
                    super.getUser().sendMessage("&cUsage: /schematic save [name]");
                    return;
                }

                SchematicParser.asyncSave(
                        user,
                        RegionUtils.getInstance().getRegion(super.getUser().to(SBPlayer.class)),
                        args.length == 2 ? args[1] : null
                );
            }
            case "load" -> {
                if (args.length != 2) {
                    super.getUser().sendMessage("&cUsage: /schematic load [name]");
                    return;
                }

                String fileName = args[1].endsWith(".json") ? args[1] : args[1] + ".json";
                Schematic schem = SchematicParser.asyncLoad(user, SBFiles.get(SBConstants.Schematics.SCHEMATIC_FOLDER + fileName));

                if (schem == null) {
                    super.getUser().sendMessage("&cError occurred in loading schematic: &o" + args[1]);
                    return;
                }

                SBPlayer player = super.getUser().to(SBPlayer.class);
                Clipboard clipboard = ClipboardUtils.getInstance().getClipboard(player);

                clipboard.add(schem);
                ClipboardUtils.getInstance().setClipboard(player, clipboard);

                System.out.println(player.clipboard());
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
