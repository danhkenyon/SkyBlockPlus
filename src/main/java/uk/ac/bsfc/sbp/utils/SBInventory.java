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


/**
 * Represents a custom interactive inventory for use in Bukkit plugins.
 *
 * This class provides methods to create and manage custom inventories
 * for players with advanced features such as button actions,
 * inventory patterns, paging, and event handling for inventory
 * interactions.
 *
 * Features:
 * - Customizable title and size (based on rows of 9 slots).
 * - Ability to set interactive buttons with actions.
 * - Pattern-based inventory filling with item mappings.
 * - Utilities to fill the inventory with items or create decorative borders.
 * - Support for next/previous page buttons linked to other inventories.
 * - Event handling for inventory clicks and closures.
 */
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

    public SBInventory fillPattern(Map<Character, ItemStack> itemMap, String pattern) {
        if (pattern.length() > inventory.getSize()) {
            throw new IllegalArgumentException("Pattern length exceeds inventory size");
        }

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            ItemStack item = itemMap.get(c);
            if (item == null && c != ' ') {
                throw new IllegalArgumentException("Pattern contains unmapped character: " + c);
            }
            if (c != ' ') {
                inventory.setItem(i, item);
            }
        }
        return this;
    }
    public SBInventory fillPattern(Map<Character, ItemStack> itemMap, String[] pattern) {
        for (String row : pattern) {
            if (row.length() > 9) {
                throw new IllegalArgumentException("Pattern length exceeds inventory size");
            }

            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                ItemStack item = itemMap.get(c);
                if (item == null && c != ' ') {
                    throw new IllegalArgumentException("Pattern contains unmapped character: " + c);
                }
                if (c != ' ') {
                    inventory.setItem(i, item);
                }
            }
        }
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

    public SBInventory addNextPageButton(int slot, ItemStack item, SBInventory nextPageInventory) {
        return setButton(slot, item, (player, event) -> nextPageInventory.open(player));
    }

    public SBInventory addPrevPageButton(int slot, ItemStack item, SBInventory prevPageInventory) {
        return setButton(slot, item, (player, event) -> prevPageInventory.open(player));
    }

    public SBInventory addCloseButton(int slot, ItemStack item) {
        return setButton(slot, item, (player, event) -> player.closeInventory());
    }


    public SBInventory fillAll(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
        return this;
    }


    public SBInventory fillBorder(ItemStack item) {
        int size = inventory.getSize();
        int rows = size / 9;
        
        if (rows < 3) {
            throw new IllegalStateException("Border fill requires at least 3 rows");
        }

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
            inventory.setItem(size - 9 + i, item);
        }

        for (int i = 1; i < rows - 1; i++) {
            inventory.setItem(i * 9, item);
            inventory.setItem(i * 9 + 8, item);
        }
        
        return this;
    }

    public SBInventory fillRow(int row, ItemStack item) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("Row index out of bounds: " + row);
        }
        
        int startSlot = row * 9;
        for (int i = 0; i < 9; i++) {
            inventory.setItem(startSlot + i, item);
        }
        
        return this;
    }

    public SBInventory fillColumn(int column, ItemStack item) {
        if (column < 0 || column >= 9) {
            throw new IllegalArgumentException("Column index out of bounds: " + column);
        }
        
        for (int i = 0; i < rows; i++) {
            inventory.setItem(i * 9 + column, item);
        }
        
        return this;
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public boolean contains(ItemStack item) {
        return inventory.contains(item);
    }

    public SBInventory removeItem(int slot) {
        inventory.setItem(slot, null);
        return this;
    }

    public SBInventory clear() {
        inventory.clear();
        actions.clear();
        return this;
    }
}