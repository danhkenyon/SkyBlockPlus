package uk.ac.bsfc.sbp.utils.menus;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import org.bukkit.inventory.ItemStack;
import uk.ac.bsfc.sbp.Main;

public class SBItemListener implements Listener {
    public static void register() {
        Main.getInstance().getServer().getPluginManager().registerEvents(new SBItemListener(), Main.getInstance());
    }

    private SBItem getSBItem(ItemStack item) {
        return SBItem.fromBukkit(item);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var sbItem = getSBItem(event.getItem());
        if (sbItem != null && sbItem.getOnInteract() != null)
            sbItem.getOnInteract().accept(event.getPlayer(), event);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        System.out.println("a");
        var sbItem = getSBItem(event.getItemDrop().getItemStack());
        System.out.println(sbItem.getName());
        if (sbItem != null && sbItem.getOnDrop() != null) {
            System.out.println("b");
            sbItem.getOnDrop().accept(event.getPlayer(), event);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickItemEvent event) {
        var sbItem = getSBItem(event.getPlayer().getPickItemStack());
        if (sbItem != null && sbItem.getOnPickup() != null)
            sbItem.getOnPickup().accept(event.getPlayer(), event);
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        var sbItem = getSBItem(newItem);
        if (sbItem != null && sbItem.getOnHeld() != null)
            sbItem.getOnHeld().accept(player, event);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        var sbItem = getSBItem(event.getItem());
        if (sbItem != null && sbItem.getOnConsume() != null)
            sbItem.getOnConsume().accept(event.getPlayer(), event);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        var sbItem = getSBItem(event.getOffHandItem());
        if (sbItem != null && sbItem.getOnSwapHand() != null)
            sbItem.getOnSwapHand().accept(event.getPlayer(), event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        var sbItem = getSBItem(event.getCurrentItem());
        if (sbItem != null && sbItem.getOnInventoryClick() != null)
            sbItem.getOnInventoryClick().accept(player, event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        for (ItemStack item : event.getNewItems().values()) {
            var sbItem = getSBItem(item);
            if (sbItem != null && sbItem.getOnInventoryDrag() != null)
                sbItem.getOnInventoryDrag().accept(player, event);
        }
    }
}
