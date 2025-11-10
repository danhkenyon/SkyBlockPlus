package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class SBInventory implements Listener {

    private static final Map<UUID, SBInventory> openInventories = new HashMap<>();
    private static boolean listenerRegistered = false;

    private final Component title;
    private final int rows;
    private final Inventory inventory;
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> actions = new HashMap<>();
    private BiConsumer<Player, Inventory> onClose;

    public SBInventory(Component title, int rows){
        this.title = title;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows*9, title);

        registerListener();
    }

    private void registerListener(){
        if(!listenerRegistered){
            JavaPlugin plugin = Main.getInstance();
            Bukkit.getPluginManager().registerEvents(this, plugin);
            listenerRegistered = true;
        }
    }

    public SBInventory setButton(int slot, ItemStack item, BiConsumer<Player, InventoryClickEvent> action){
        inventory.setItem(slot, item);
        if (action != null) actions.put(slot, action);
        return this;
    }


    public SBInventory setItem(int slot, ItemStack item){
        inventory.setItem(slot, item);
        return this;
    }

    public SBInventory onClose(BiConsumer<Player, Inventory> onClose){
        this.onClose = onClose;
        return this;
    }

    public void open(Player player){
        openInventories.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent it){
        if (!(it.getWhoClicked() instanceof Player player)) return;
        SBInventory inv = openInventories.get(player.getUniqueId());
        if (inv == null) return;
        if (!it.getView().getTopInventory().equals(inv.inventory)) return;

        it.setCancelled(true);
        BiConsumer<Player, InventoryClickEvent> action = inv.actions.get(it.getRawSlot());
        if (action != null) action.accept(player, it);
    }


    @EventHandler
    public void onClose(InventoryCloseEvent it){
        if (!(it.getPlayer() instanceof Player player)) return;
        SBInventory inv = openInventories.remove(player.getUniqueId());
        if (inv != null && inv.onClose != null) inv.onClose.accept(player, inv.inventory);
    }




}
