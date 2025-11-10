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
 * SBInventory is a utility class for managing custom inventories with interaction events
 * within a Minecraft Bukkit/Spigot plugin. It includes features such as custom titles,
 * clickable actions, and events triggered on inventory close.
 *
 * This class implements the {@link Listener} interface to handle inventory-related
 * events such as {@link InventoryClickEvent} and {@link InventoryCloseEvent}.
 * It ensures interaction actions and custom behaviors can be easily defined for specific slots
 * in the inventory.
 *
 * When an inventory is opened using this class, it is automatically registered to listen
 * for player interactions and manages the inventory's behavior.
 *
 * Fields:
 * - `openInventories`: Maintains a map of currently open inventories by player UUID.
 * - `listenerRegistered`: Ensures the class event listener is only registered once.
 * - `title`: The title of the custom inventory.
 * - `rows`: The number of rows in the custom inventory.
 * - `inventory`: The Bukkit Inventory instance created to represent the custom inventory.
 * - `actions`: A map of slot indices and their corresponding click actions, allowing
 *    custom logic to be executed when a slot is clicked.
 * - `onClose`: A consumer defining the logic to execute when the inventory is closed.
 *
 * Constructor:
 * - `SBInventory(Component title, int rows)`: Creates a new instance of an SBInventory with
 *    the specified title and number of rows, initializing the internal inventory.
 *
 * Methods:
 * - `setButton(int slot, ItemStack item, BiConsumer<Player, InventoryClickEvent> action)`:
 *    Sets a button in the inventory at a given slot with an associated action upon being clicked.
 * - `setItem(int slot, ItemStack item)`: Sets a static item in the inventory at the given slot
 *    without defining any click interaction.
 * - `onClose(BiConsumer<Player, Inventory> onClose)`: Allows setting a consumer action to
 *    execute when the inventory is closed.
 * - `open(Player player)`: Opens the custom inventory for the specified player, registering it
 *    to handle interaction events.
 *
 * Event Handlers:
 * - `onClick(InventoryClickEvent event)`: Handles inventory click interactions, executing
 *    predefined actions for specific slots if applicable.
 * - `onClose(InventoryCloseEvent event)`: Handles inventory close events, executing the
 *    logic defined in the onClose consumer if such logic has been set.
 *
 * Note:
 * - The registerListener method ensures the class's event listener is registered only once.
 * - Care should be taken when assigning actions and onClose logic to avoid memory leaks
 *   by ensuring inventories are properly removed from `openInventories` after closure.
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
