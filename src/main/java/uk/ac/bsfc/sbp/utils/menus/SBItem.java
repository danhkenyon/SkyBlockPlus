package uk.ac.bsfc.sbp.utils.menus;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.Wrapper;
import uk.ac.bsfc.sbp.utils.data.JSON;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

public class SBItem extends Wrapper<ItemStack> {
    public static final Map<UUID, SBItem> REGISTRY = new HashMap<>();

    private @NotNull final UUID uuid;
    private @NotNull final Component name;
    private final int amount;
    private @NotNull final Material blockType;
    private @NotNull final Component[] lore;
    private final boolean stackable;

    protected SBItem(
            @Nullable Component name,
            int amount,
            @NotNull Material blockType,
            @Nullable Component[] lore,
            boolean stackable
    ) {
        this.uuid = (stackable) ? new UUID(0, 0) : UUID.randomUUID();
        this.name = (name == null) ? Component.text(this.formatMaterialName()) : name;
        this.amount = amount;
        this.blockType = blockType;
        this.lore = (lore == null) ?  new Component[0] : lore;
        this.stackable = stackable;

        REGISTRY.put(uuid, this);
    }

    private BiConsumer<Player, PlayerInteractEvent> onInteract;
    private BiConsumer<Player, PlayerDropItemEvent> onDrop;
    private BiConsumer<Player, PlayerPickItemEvent> onPickup;
    private BiConsumer<Player, PlayerItemHeldEvent> onHeld;
    private BiConsumer<Player, PlayerItemConsumeEvent> onConsume;
    private BiConsumer<Player, PlayerSwapHandItemsEvent> onSwapHand;
    private BiConsumer<Player, InventoryClickEvent> onInventoryClick;
    private BiConsumer<Player, InventoryDragEvent> onInventoryDrag;

    public SBItem onInteract(BiConsumer<Player, PlayerInteractEvent> action) {
        this.onInteract = action;
        return this;
    }
    public SBItem onDrop(BiConsumer<Player, PlayerDropItemEvent> action) {
        this.onDrop = action;
        return this;
    }
    public SBItem onPickup(BiConsumer<Player, PlayerPickItemEvent> action) {
        this.onPickup = action;
        return this;
    }
    public SBItem onHeld(BiConsumer<Player, PlayerItemHeldEvent> action) {
        this.onHeld = action;
        return this;
    }
    public SBItem onConsume(BiConsumer<Player, PlayerItemConsumeEvent> action) {
        this.onConsume = action;
        return this;
    }
    public SBItem onSwapHand(BiConsumer<Player, PlayerSwapHandItemsEvent> action) {
        this.onSwapHand = action;
        return this;
    }
    public SBItem onInventoryClick(BiConsumer<Player, InventoryClickEvent> action) {
        this.onInventoryClick = action;
        return this;
    }
    public SBItem onInventoryDrag(BiConsumer<Player, InventoryDragEvent> action) {
        this.onInventoryDrag = action;
        return this;
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }
    public @NotNull Component getName() {
        return name;
    }
    public int getAmount() {
        return amount;
    }
    public @NotNull Material getBlockType() {
        return blockType;
    }
    public @NotNull Component[] getLore() {
        return lore;
    }
    public boolean isStackable() {
        return stackable;
    }

    public BiConsumer<Player, PlayerInteractEvent> getOnInteract() {
        return onInteract;
    }
    public BiConsumer<Player, PlayerDropItemEvent> getOnDrop() {
        return onDrop;
    }
    public BiConsumer<Player, PlayerPickItemEvent> getOnPickup() {
        return onPickup;
    }
    public BiConsumer<Player, PlayerItemHeldEvent> getOnHeld() {
        return onHeld;
    }
    public BiConsumer<Player, PlayerItemConsumeEvent> getOnConsume() {
        return onConsume;
    }
    public BiConsumer<Player, PlayerSwapHandItemsEvent> getOnSwapHand() {
        return onSwapHand;
    }
    public BiConsumer<Player, InventoryClickEvent> getOnInventoryClick() {
        return onInventoryClick;
    }
    public BiConsumer<Player, InventoryDragEvent> getOnInventoryDrag() {
        return onInventoryDrag;
    }

    private String formatMaterialName() {
        char[] chars = this.getBlockType().name().toLowerCase().replace('_', ' ').toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.copyValueOf(chars);
    }

    @Override
    public ItemStack toBukkit() {
        ItemStack item = new ItemStack(blockType, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(name);

            if (lore.length > 0) {
                meta.lore(Arrays.asList(lore));
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(
                    NKeys.getKey("item-uuid"),
                    PersistentDataType.STRING,
                    uuid.toString()
            );

            item.setItemMeta(meta);
        }

        return item;
    }
    public static SBItem fromBukkit(ItemStack item) {
        System.out.println(item.getItemMeta());
        if (item == null || !item.hasItemMeta()) return null;
        var meta = item.getItemMeta();
        var container = meta.getPersistentDataContainer();

        String uuidStr = container.get(NKeys.getKey("item-uuid"), PersistentDataType.STRING);
        System.out.println(uuidStr);
        if (uuidStr == null) {
            return null;
        }

        UUID uuid = UUID.fromString(uuidStr);

        if (!uuid.equals(new UUID(0, 0))) {
            System.out.println(REGISTRY);
            System.out.println(REGISTRY.get(uuid));
            return REGISTRY.get(uuid);
        }

        Component name = meta.hasDisplayName() ? meta.displayName() : Component.text(item.getType().name());
        System.out.println(name);
        Component[] lore = meta.hasLore() && meta.lore() != null ? meta.lore().toArray(new Component[0]) : new Component[0];
        System.out.println(Arrays.toString(lore));
        return new SBItem(name, item.getAmount(), item.getType(), lore, true);
    }

    @SuppressWarnings("unchecked")
    public static void loadRegistry() {
        var file = JSON.get("item-registry");
        Map<String, Object> rawData = (Map<String, Object>) file.get("items");
        if (rawData == null) return;

        for (Map.Entry<String, Object> entry : rawData.entrySet()) {
            String uuidStr = entry.getKey();
            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();

            String nameStr = String.valueOf(itemData.getOrDefault("name", "Unknown"));
            String materialStr = String.valueOf(itemData.getOrDefault("material", "STONE"));
            int amount = ((Number) itemData.getOrDefault("amount", 1)).intValue();
            boolean stackable = (boolean) itemData.getOrDefault("stackable", false);

            List<?> rawLore = (List<?>) itemData.getOrDefault("lore", List.of());
            String[] loreStrings = rawLore.stream()
                    .map(Object::toString)
                    .toArray(String[]::new);

            Component name = Component.text(nameStr);
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if (material == null) material = Material.STONE;

            SBItem item = new SBItem(name, amount, material, Arrays.stream(loreStrings)
                    .map(Component::text)
                    .toArray(Component[]::new),
                    stackable);

            if (!stackable) {
                try {
                    java.lang.reflect.Field uuidField = SBItem.class.getDeclaredField("uuid");
                    uuidField.setAccessible(true);
                    uuidField.set(item, UUID.fromString(uuidStr));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            REGISTRY.put(UUID.fromString(uuidStr), item);
        }
    }
    public static void saveRegistry() {
        var file = JSON.get("item-registry");
        Map<String, SBItemData> data = new LinkedHashMap<>();

        for (SBItem item : REGISTRY.values()) {
            if (item.isStackable() && item.getUniqueId().equals(new UUID(0, 0))) continue;

            String[] loreStrings = Arrays.stream(item.getLore())
                    .map(Component::toString)
                    .toArray(String[]::new);

            SBItemData itemData = new SBItemData(
                    item.getUniqueId().toString(),
                    item.getName().toString(),
                    item.getBlockType().name(),
                    item.getAmount(),
                    loreStrings,
                    item.isStackable()
            );
            data.put(item.getUniqueId().toString(), itemData);
        }

        file.set("items", data);
        file.save();
    }

    public boolean equals(SBItem other) {
        return other.getUniqueId() == uuid && other.getName().equals(name) && other.getAmount() == amount;
    }
}
