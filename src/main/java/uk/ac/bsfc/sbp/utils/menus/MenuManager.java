package uk.ac.bsfc.sbp.utils.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
    private static final Map<UUID, SBMenu> openMenus = new HashMap<>();

    private MenuManager() {
    }

    private static MenuManager INSTANCE;
    public static MenuManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MenuManager();
        }
        return INSTANCE;
    }

    public SBMenu getMenu(UUID uuid) {
        return openMenus.get(uuid);
    }
    public Map<UUID, SBMenu> getMenus() {
        return openMenus;
    }
    public List<SBMenu> getMenusList() {
        return openMenus.values().stream().toList();
    }

    public void addMenu(UUID uuid, SBMenu menu) {
        openMenus.put(uuid, menu);
    }
    public void removeMenu(UUID uuid) {
        openMenus.remove(uuid);
    }
}
