package uk.ac.bsfc.sbp.utils.user;

public enum Action {
    RIGHT_CLICK_BLOCK,
    RIGHT_CLICK_AIR,
    LEFT_CLICK_BLOCK,
    LEFT_CLICK_AIR,
    PHYSICAL;

    public static boolean isLeftClick(org.bukkit.event.block.Action action) {
        return action == org.bukkit.event.block.Action.LEFT_CLICK_AIR || action == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
    }
    public static boolean isRightClick(org.bukkit.event.block.Action action) {
        return action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
    }
    public static boolean isPhysical(org.bukkit.event.block.Action action) {
        return action == org.bukkit.event.block.Action.PHYSICAL;
    }
}
