package uk.ac.bsfc.sbp.core.commands.schematic;

import org.bukkit.Material;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.schematic.Region;
import uk.ac.bsfc.sbp.utils.schematic.RegionUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.Arrays;
import java.util.List;

public class SetCommand extends SBCommand {
    public SetCommand() {
        super();

        super.name("/set");
        super.description("Set blocks in a schematic to the selected area.");
        super.usage("//set <block>");
    }

    @Override
    public void execute() {
        Region region = RegionUtils.getInstance().getRegion(super.getUser().to(SBPlayer.class));

        if (region == null || !region.isComplete()) {
            super.getUser().sendMessage("&cYou must first select two positions using //pos1 and //pos2.");
            return;
        }

        if (super.args().length < 1) {
            super.getUser().sendMessage("&cYou must specify a block to set.");
            return;
        }

        String block = super.args()[0];
        Material blockMaterial = Material.getMaterial(block.toUpperCase());

        if (blockMaterial == null || !blockMaterial.isBlock()) {
            super.getUser().sendMessage("&c'&f&l" + block + "&c' is not a valid block!");
            return;
        }

        region.asyncFill(blockMaterial);
        super.getUser().sendMessage("&aSuccessfully set the selected area to &f" + blockMaterial.name() + "&a.");
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
