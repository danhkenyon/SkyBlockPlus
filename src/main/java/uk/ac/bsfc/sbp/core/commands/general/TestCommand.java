package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.menus.ItemBuilder;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.menus.SBMenu;
import uk.ac.bsfc.sbp.utils.user.Action;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.HashMap;
import java.util.Map;

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
        Map<Character, SBItem> pattern = new HashMap<>();
        pattern.put('x', ItemBuilder.create(Material.RED_STAINED_GLASS_PANE).build());
        pattern.put('y', ItemBuilder.create(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build());
        pattern.put('a', ItemBuilder.create(Material.DIAMOND).build());
        pattern.put('b', ItemBuilder.create(Material.BARRIER).build());
        
        SBMenu inventory = SBMenu.create(mm.deserialize("<rainbow>Dans a cunt"), 3)
                .setButton(12, ItemBuilder.create(Material.DIAMOND).build(),((evPlayer, event) -> evPlayer.sendMessage("cunt")))
                .setButton(14, ItemBuilder.create(Material.BARRIER).build(), ((evPlayer, event) -> evPlayer.sendMessage("cunt2")))
                .onClose((evPlayer, inv) -> player.sendMessage("Closed"))
                .fillPattern(pattern, "xyxyxyxyx", "yxyaybyxy", "xyxyxyxyx");

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

        player.getInventory().setItem(0, item.toBukkit());
    }
}

