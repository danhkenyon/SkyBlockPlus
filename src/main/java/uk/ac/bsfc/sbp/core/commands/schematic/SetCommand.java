package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Material;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.Region;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.Arrays;
import java.util.List;

/**
 * The SetCommand class provides functionality to set blocks in a schematic
 * to a selected region in the game. It extends the SBCommand class and is
 * used to handle the `/set` command execution and its related features.
 *
 * The command allows players to fill a selected region with a specific block
 * type, which is determined by user input in the command. The class also
 * provides tab-completion suggestions for block names.
 *
 * This class ensures that the executing user is a player, validates the
 * selected region, and checks for valid block types. The block filling
 * operation is performed asynchronously for better performance.
 *
 * Key features include:
 * - Command name: `/set`
 * - Description: "Set blocks in a schematic to the selected area."
 * - Usage: `//set <block>`
 *
 * Methods:
 * - {@link #execute()}: Handles the logic for executing the command. Ensures
 *   valid user, region, and block type, then fills the selected region.
 * - {@link #suggestions(int)}: Provides tab-completion suggestions for block
 *   types based on user input.
 */
public class SetCommand extends SBCommand {
    public SetCommand() {
        super();

        super.name("/set");
        super.description("Set blocks in a schematic to the selected area.");
        super.usage("//set <block>");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer player)) {
            user.sendMessage("{messages.player-only-command}");
            return;
        }
        Region region = RegionUtils.getInstance().getRegion(player);
        if (region == null || !region.isComplete()) {
            player.sendMessage("{messages.world-edit.no-region-selected}");
            return;
        }

        if (super.args().length < 1) {
            player.sendMessage("{messages.world-edit.null-block-set}");
            return;
        }
        String block = super.args()[0];
        Material blockMaterial = Material.getMaterial(block.toUpperCase());

        if (blockMaterial == null || !blockMaterial.isBlock()) {
            player.sendMessage("<red>'<white><b>%block%<red>' is not a valid block!", Placeholder.of("%block%", block));
            return;
        }

        region.asyncFill(blockMaterial);
        player.sendMessage("{messages.world-edit.async} <green>Successfully set the selected area to <white>" + blockMaterial.name() + "<green>.");
    }

    @Override
    public List<String> suggestions(int index) {
        return switch (index) {
            case 0 -> Arrays.stream(Material.values()).toList().stream()
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .toList();
            default -> List.of();
        };
    }
}
