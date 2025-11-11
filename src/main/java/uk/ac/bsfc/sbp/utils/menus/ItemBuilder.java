package uk.ac.bsfc.sbp.utils.menus;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import java.util.function.BiConsumer;

public class ItemBuilder {
    private final Material material;

    private Component name;
    private int amount = 1;
    private Component[] lore = new Component[0];
    private boolean stackable = false;

    private BiConsumer<Player, PlayerInteractEvent> onInteract;
    private BiConsumer<Player, PlayerDropItemEvent> onDrop;
    private BiConsumer<Player, PlayerPickItemEvent> onPickup;
    private BiConsumer<Player, PlayerItemHeldEvent> onHeld;
    private BiConsumer<Player, PlayerItemConsumeEvent> onConsume;
    private BiConsumer<Player, PlayerSwapHandItemsEvent> onSwapHand;
    private BiConsumer<Player, InventoryClickEvent> onInventoryClick;
    private BiConsumer<Player, InventoryDragEvent> onInventoryDrag;

    private ItemBuilder(Material material) {
        this.material = material;
    }
    public static ItemBuilder create(Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder setName(Component name) {
        this.name = name;
        return this;
    }
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    public ItemBuilder setLore(Component... lore) {
        this.lore = lore;
        return this;
    }
    public ItemBuilder setStackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    public ItemBuilder onInteract(BiConsumer<Player, PlayerInteractEvent> action) {
        this.onInteract = action;
        return this;
    }
    public ItemBuilder onDrop(BiConsumer<Player, PlayerDropItemEvent> action) {
        this.onDrop = action;
        return this;
    }
    public ItemBuilder onPickup(BiConsumer<Player, PlayerPickItemEvent> action) {
        this.onPickup = action;
        return this;
    }
    public ItemBuilder onHeld(BiConsumer<Player, PlayerItemHeldEvent> action) {
        this.onHeld = action;
        return this;
    }
    public ItemBuilder onConsume(BiConsumer<Player, PlayerItemConsumeEvent> action) {
        this.onConsume = action;
        return this;
    }
    public ItemBuilder onSwapHand(BiConsumer<Player, PlayerSwapHandItemsEvent> action) {
        this.onSwapHand = action;
        return this;
    }
    public ItemBuilder onInventoryClick(BiConsumer<Player, InventoryClickEvent> action) {
        this.onInventoryClick = action;
        return this;
    }
    public ItemBuilder onInventoryDrag(BiConsumer<Player, InventoryDragEvent> action) {
        this.onInventoryDrag = action;
        return this;
    }

    public SBItem build() {
        SBItem item = new SBItem(name, amount, material, lore, stackable);

        if (onInteract != null) item.onInteract(onInteract);
        if (onDrop != null) item.onDrop(onDrop);
        if (onPickup != null) item.onPickup(onPickup);
        if (onHeld != null) item.onHeld(onHeld);
        if (onConsume != null) item.onConsume(onConsume);
        if (onSwapHand != null) item.onSwapHand(onSwapHand);
        if (onInventoryClick != null) item.onInventoryClick(onInventoryClick);
        if (onInventoryDrag != null) item.onInventoryDrag(onInventoryDrag);

        return item;
    }
}
