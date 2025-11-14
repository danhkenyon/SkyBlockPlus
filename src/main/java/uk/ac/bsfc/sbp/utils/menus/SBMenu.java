package uk.ac.bsfc.sbp.utils.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import uk.ac.bsfc.sbp.utils.Wrapper;

import java.util.HashMap;
import java.util.Map;
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
public class SBMenu extends Wrapper<Inventory> {
    private final Component title;
    private final int rows;
    private final Inventory inventory;

    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> actions = new HashMap<>();
    private BiConsumer<Player, Inventory> onClose;

    private SBMenu(final Component title, final int rows){
        this.title = title;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows*9, this.title);
    }
    public static SBMenu create(final Component title, final int rows){
        return new SBMenu(title, rows);
    }
    public static SBMenu create(final Component title, final Inventory inventory){
        return new SBMenu(title, inventory.getSize() / 9);
    }

    public SBMenu setButton(int slot, SBItem item, BiConsumer<Player, InventoryClickEvent> action){
        inventory.setItem(slot, item.toBukkit());
        if (action != null) {
            actions.put(slot, action);
        }
        return this;
    }
    public SBMenu onClose(BiConsumer<Player, Inventory> onClose){
        this.onClose = onClose;
        return this;
    }

    public void open(Player player){
        MenuManager.getInstance().addMenu(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public SBMenu addItem(SBItem item) {
        this.toBukkit().addItem(item.toBukkit());
        return this;
    }
    public SBMenu addItems(SBItem ... items) {
        for (SBItem item : items) {
            this.addItem(item);
        }
        return this;
    }
    public SBMenu setItem(int slot, SBItem item) {
        this.toBukkit().setItem(slot, item.toBukkit());
        return this;
    }
    public SBMenu setItems(int slot, SBItem ... items) {
        for (SBItem item : items) {
            this.setItem(slot, item);
        }
        return this;
    }

    public SBMenu removeItem(int slot) {
        inventory.setItem(slot, null);
        return this;
    }

    public boolean contains(SBItem item) {
        return inventory.contains(item.toBukkit());
    }
    public SBMenu clear() {
        inventory.clear();
        actions.clear();
        return this;
    }

    public SBMenu fillAll(SBItem item) {
        for (int i = 0; i < this.getRows() * 9; i++) {
            this.setItem(i, item);
        }
        return this;
    }
    public SBMenu fillBorder(SBItem item) {
        if (rows < 3) {
            throw new IllegalStateException("Border fill requires at least 3 rows");
        }

        for (int i = 0; i < 9; i++) {
            this.setItem(i, item);
            this.setItem((rows * 9) - 9 + i, item);
        }

        for (int i = 1; i < rows - 1; i++) {
            this.setItem(i * 9, item);
            this.setItem(i * 9 + 8, item);
        }

        return this;
    }
    public SBMenu fillRow(int row, SBItem item) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("Row index out of bounds: " + row);
        }

        int startSlot = row * 9;
        for (int i = 0; i < 9; i++) {
            this.setItem(startSlot + i, item);
        }

        return this;
    }
    public SBMenu fillColumn(int column, SBItem item) {
        if (column < 0 || column >= 9) {
            throw new IllegalArgumentException("Column index out of bounds: " + column);
        }

        for (int i = 0; i < rows; i++) {
            this.setItem(i * 9 + column, item);
        }

        return this;
    }

    public SBMenu fillPattern(Map<Character, SBItem> itemMap, String pattern) {
        if (pattern.length() > inventory.getSize()) {
            throw new IllegalArgumentException("Pattern length exceeds inventory size");
        }

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            SBItem item = itemMap.get(c);
            if (item == null && c != ' ') {
                throw new IllegalArgumentException("Pattern contains unmapped character: " + c);
            }
            if (c != ' ') {
                this.setItem(i, item);
            }
        }
        return this;
    }
    public SBMenu fillPattern(Map<Character, SBItem> itemMap, String ... pattern) {
        if (pattern.length > inventory.getSize()) {
            throw new IllegalArgumentException("Pattern length exceeds inventory size");
        }

        for (String row : pattern) {
            if (row.length() > 9) {
                throw new IllegalArgumentException("Row length exceeds inventory 9.");
            }

            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                SBItem item = itemMap.get(c);
                if (item == null && c != ' ') {
                    throw new IllegalArgumentException("Character not mapped: " + c);
                }
                if (c != ' ') {
                    this.setItem(i, item);
                }
            }
        }
        return this;
    }

    public SBMenu addNextPageButton(int slot, SBItem item, SBMenu nextPageInventory) {
        return this.setButton(slot, item, (player, event) -> nextPageInventory.open(player));
    }
    public SBMenu addPrevPageButton(int slot, SBItem item, SBMenu prevPageInventory) {
        return this.setButton(slot, item, (player, event) -> prevPageInventory.open(player));
    }
    public SBMenu addCloseButton(int slot, SBItem item) {
        return this.setButton(slot, item, (player, event) -> player.closeInventory());
    }

    public SBItem getItem(int slot) {
        return SBItem.fromBukkit(inventory.getItem(slot));
    }
    public Component getTitle() {
        return title;
    }
    public int getRows() {
        return rows;
    }
    public Map<Integer, BiConsumer<Player, InventoryClickEvent>> getActions() {
        return actions;
    }
    public BiConsumer<Player, Inventory> getOnClose() {
        return onClose;
    }

    @Override
    public Inventory toBukkit() {
        return inventory;
    }
}