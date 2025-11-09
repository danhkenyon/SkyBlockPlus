package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.game.SBServer;

import java.util.ArrayList;
import java.util.List;

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
        SBServer.broadcast(super.getUser(), "<green>Test command executed!");

        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta meta = itemStack.getItemMeta();
        Component lorelol = MiniMessage.miniMessage().deserialize("<white>Look at my <sprite:blocks:block/oak_log>!").decoration(TextDecoration.ITALIC, false);
        List<Component> list = new ArrayList<>();
        list.add(lorelol);
        meta.lore(list);

        itemStack.setItemMeta(meta);

        HoverEvent<HoverEvent.ShowItem> hover = HoverEvent.showItem(
                itemStack.asHoverEvent().value()
        );

        itemStack.displayName();
        Component itemName = itemStack.displayName();

        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(itemName.hoverEvent(hover));
        }

        user.toBukkit(Player.class).getInventory().addItem(itemStack);
    }
}

