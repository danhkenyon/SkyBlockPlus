package uk.ac.bsfc.sbp.utils.menus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.menus.MenuManager;
import uk.ac.bsfc.sbp.utils.menus.SBMenu;

import java.util.function.BiConsumer;

public class SBMenuListener implements Listener {
    public static void register() {
        Main.getInstance().getServer().getPluginManager().registerEvents(new SBMenuListener(), Main.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent it){
        if (!(it.getWhoClicked() instanceof Player player)) return;
        SBMenu inv = MenuManager.getInstance().getMenus().get(player.getUniqueId());
        if (inv == null) return;
        if (!it.getView().getTopInventory().equals(inv.toBukkit())) return;

        it.setCancelled(true);
        BiConsumer<Player, InventoryClickEvent> action = inv.getActions().get(it.getRawSlot());
        if (action != null) action.accept(player, it);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent it){
        if (!(it.getPlayer() instanceof Player player)) return;
        SBMenu inv = MenuManager.getInstance().getMenus().remove(player.getUniqueId());
        if (inv != null && inv.getOnClose() != null) {
            inv.getOnClose().accept(player, inv.toBukkit());
        }
    }
}
