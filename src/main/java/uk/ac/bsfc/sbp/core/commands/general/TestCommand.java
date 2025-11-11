package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.SBInventory;
import uk.ac.bsfc.sbp.utils.command.SBCommand;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public class TestCommand extends SBCommand {
    public TestCommand() {
        super();
        super.name("test");
        super.description("A test command.");
        super.usage("/test");
        super.permission(null);

        super.aliases("test-command");
    }
    @Override
    public void execute() {
        Player player = user.toBukkit(Player.class);
        Map<Character, ItemStack> pattern = new HashMap<>();
        pattern.put('x', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        pattern.put('y', new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
        pattern.put('a', new ItemStack(Material.DIAMOND));
        pattern.put('b', new ItemStack(Material.BARRIER));

        SBInventory inventory = new SBInventory(MiniMessage.miniMessage().deserialize("<red>Dans a cunt"), 3)
                .setButton(12, new ItemStack(Material.DIAMOND),((evPlayer, event) -> {evPlayer.sendMessage("cunt");}))
                .setButton(14, new ItemStack(Material.BARRIER), ((evPlayer, event) -> {evPlayer.sendMessage("cunt2");}))
                .onClose((evPlayer, inv) -> player.sendMessage("Closed"))
                .fillPattern(pattern, "xyxyxyxyx"+"yxyaybyxy"+ "xyxyxyxyx");

        inventory.open(player);
    }


}

