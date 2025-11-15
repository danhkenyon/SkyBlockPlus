package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.items.CountItem;
import uk.ac.bsfc.sbp.utils.menus.ItemBuilder;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.user.Action;
import uk.ac.bsfc.sbp.utils.user.SBUser;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public class TestCommand extends SBCommand {

    MiniMessage mm = MiniMessage.miniMessage();

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

        SBItem item = ItemBuilder.create(Material.WOODEN_AXE)
                .setName(mm.deserialize("<red><bold>WorldEdit Axe"))
                .setAmount(1)
                .setLore(
                        mm.deserialize("<gray>This can be used to select"),
                        mm.deserialize("<gray>which server to join!")
                )
                .setStackable(false)
                .onDrop((pl, event) -> event.setCancelled(true))
                .onInteract((pl, event) -> {
                    if (Action.isRightClick(event.getAction())) {
                        user.sudo(SBUser.console(), "pos2");
                    }else if (Action.isLeftClick(event.getAction())) {
                        user.sudo(SBUser.console(), "pos1");
                    }
                }).build();

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# . . . . . . . #",
                        "# # # # # # # # #")
                .addIngredient('#', new SimpleItem(new xyz.xenondevs.invui.item.builder.ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('.', new CountItem())
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Test GUI")
                .setGui(gui)
                .build();

        window.open();

        player.getInventory().setItem(0, item.toBukkit());
    }
}

