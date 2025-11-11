package uk.ac.bsfc.sbp.utils.menus;

public record SBItemData(
        String uuid,
        String name,
        String material,
        int amount,
        String[] lore,
        boolean stackable
) {}