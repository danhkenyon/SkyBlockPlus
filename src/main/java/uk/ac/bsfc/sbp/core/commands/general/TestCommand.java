package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.game.SBServer;
import uk.ac.bsfc.sbp.utils.menus.ItemBuilder;
import uk.ac.bsfc.sbp.utils.menus.SBInventory;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

        SBItem item = ItemBuilder.create(Material.WOODEN_AXE)
                .setName(MiniMessage.miniMessage().deserialize("<red><bold>WorldEdit Axe"))
                .setAmount(1)
                .setLore(
                        MiniMessage.miniMessage().deserialize("<gray>This can be used to select"),
                        MiniMessage.miniMessage().deserialize("<gray>which server to join!")
                )
                .setStackable(false)
                .onDrop((pl, event) -> event.setCancelled(true))
                .onInteract((pl, event) -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR ||  event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        user.sudo(SBUser.console(), "paste");
                    }
                }).build();

        player.getInventory().setItem(0, item.toBukkit());
    }


}

