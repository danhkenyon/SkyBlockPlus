package uk.ac.bsfc.sbp.utils.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;


public abstract class SBEntity {

    protected final Entity entity;
    protected int stackSize;
    protected static final MiniMessage mm = MiniMessage.miniMessage();

    public SBEntity(Entity entity, int initialStack) {
        this.entity = entity;
        this.stackSize = initialStack;
        updateNametag();
    }

    public void initialize() {
        updateNametag();
    }

    public Entity getEntity() {
        return entity;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int newSize) {
        this.stackSize = Math.max(1, newSize);
        updateNametag();
    }

    public void incrementStack(int amount) {
        this.stackSize += amount;
        updateNametag();
    }

    public void decrementStack(int amount) {
        this.stackSize = Math.max(1, this.stackSize - amount);
        updateNametag();
    }

    protected void updateNametag() {
        if (!entity.isValid()) return;
        entity.setCustomNameVisible(true);
        entity.customName(buildDisplayName());
    }

    protected Component buildDisplayName() {
        String typeName = getDisplayName();
        if (typeName == null || typeName.isEmpty()) typeName = "Unknown";
        String msg = "<gold>x" + stackSize + " <yellow>" + typeName + "</yellow></gold>";
        return mm.deserialize(msg);
    }

    public void kill() {
        if (entity.isValid()) entity.remove();
    }


    protected abstract String getDisplayName();
}