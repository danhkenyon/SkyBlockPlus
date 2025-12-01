package uk.ac.bsfc.sbp.utils.config;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class ConfigEditorUI {

    private ConfigEditorUI() {}

    public static void openRootEditor(Player player, Class<?> configClass) {
        Object config = ConfigManager.getConfig(configClass);
        if (config == null) {
            player.sendMessage(Component.text("Config not loaded: " + configClass.getSimpleName()));
            return;
        }
        openEditor(player, config, configClass.getSimpleName());
    }

    public static void openEditor(Player player, Object instance, String title) {
        ConfigNode.ObjectNode root = ConfigNodeBuilder.buildRoot(instance);
        showObjectDialog(player, root, instance, title);
    }

    private static void showObjectDialog(Player player, ConfigNode.ObjectNode node, Object instance, String title) {
        List<ActionButton> buttons = new ArrayList<>();
        List<DialogBody> body = List.of(
                DialogBody.plainMessage(Component.text(title)),
                DialogBody.plainMessage(Component.text("Click a field to edit:"))
        );

        for (ConfigNode child : node.children()) {
            buttons.add(buttonForNode(player, node, child, instance));
        }

        ActionButton reloadBtn = ActionButton.builder(Component.text("Reload"))
                .action(DialogAction.customClick((r, aud) -> {
                    try {
                        Class<?> rootClass = findRootConfigClass(instance);
                        ConfigManager.reloadConfig(rootClass);
                        Object fresh = ConfigManager.getConfig(rootClass);
                        showObjectDialog(player, ConfigNodeBuilder.buildRoot(fresh), fresh, rootClass.getSimpleName());
                    } catch (Exception e) {
                        player.sendMessage(Component.text("§cFailed to reload: " + e.getMessage()));
                        SBLogger.err(e.getMessage());
                    }
                }, ClickCallback.Options.builder().build()))
                .build();

        buttons.add(reloadBtn);

        ActionButton exit = ActionButton.builder(Component.text("Exit"))
                .action(DialogAction.customClick((r, aud) -> {}, ClickCallback.Options.builder().build()))
                .build();

        Dialog dialog = Dialog.create(builder -> builder
                .empty()
                .base(DialogBase.builder(Component.text(node.name()))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(buttons, exit, 1))
        );

        player.showDialog(dialog);
    }

    private static ActionButton buttonForNode(Player player, ConfigNode.ObjectNode parentNode, ConfigNode child, Object parentInstance) {
        Component label = Component.text(displayFor(child));
        return ActionButton.builder(label)
                .action(DialogAction.customClick((view, audience) -> {
                    try {
                        switch (child) {
                            case ConfigNode.BooleanNode b -> handleBooleanClick(player, parentNode, b, parentInstance);
                            case ConfigNode.IntNode i -> handlePrimitiveClick(player, parentInstance, i);
                            case ConfigNode.DoubleNode d -> handlePrimitiveClick(player, parentInstance, d);
                            case ConfigNode.StringNode s -> handlePrimitiveClick(player, parentInstance, s);
                            case ConfigNode.ListNode l -> handleListClick(player, parentNode, l, parentInstance);
                            case ConfigNode.ObjectNode o -> handleObjectClick(player, o, parentInstance);
                        }
                    } catch (Exception e) {
                        SBLogger.err(e.getMessage());
                        player.sendMessage(Component.text("Error opening editor: " + e.getMessage()));
                    }
                }, ClickCallback.Options.builder().build()))
                .build();
    }

    private static String displayFor(ConfigNode node) {
        return switch (node) {
            case ConfigNode.BooleanNode b -> b.name() + ": " + (Boolean.TRUE.equals(b.value()) ? "Enabled" : "Disabled");
            case ConfigNode.IntNode i -> i.name() + ": " + i.value();
            case ConfigNode.DoubleNode d -> d.name() + ": " + d.value();
            case ConfigNode.StringNode s -> s.name() + ": " + s.value();
            case ConfigNode.ListNode l -> l.name() + ": [" + String.join(", ", l.value()) + "]";
            case ConfigNode.ObjectNode o -> o.name() + " →";
        };
    }

    private static void handleBooleanClick(Player player, ConfigNode.ObjectNode parentNode, ConfigNode.BooleanNode node, Object parentInstance) throws Exception {
        Field field = findField(parentInstance, node.name());
        field.setAccessible(true);
        boolean current = Boolean.TRUE.equals(field.get(parentInstance));
        field.set(parentInstance, !current);
        Object root = findRootConfigInstance(parentInstance);
        if (root != null) ConfigManager.saveConfig(root);
        showObjectDialog(player, ConfigNodeBuilder.buildRoot(parentInstance), parentInstance, parentInstance.getClass().getSimpleName());
    }

    private static void handlePrimitiveClick(Player player, Object parentInstance, ConfigNode node) throws Exception {
        Field field = findField(parentInstance, node.name());
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (type == int.class || type == Integer.class) {
            InputDialogs.openNumberEditor(player, parentInstance, parentInstance, field, Integer.MIN_VALUE, Integer.MAX_VALUE, () -> {
                showObjectDialog(player, ConfigNodeBuilder.buildRoot(parentInstance), parentInstance, parentInstance.getClass().getSimpleName());
            });
        } else if (type == double.class || type == Double.class || type == float.class || type == Float.class) {
            InputDialogs.openNumberEditor(player, parentInstance, parentInstance, field, -Float.MAX_VALUE, Float.MAX_VALUE, () -> {
                showObjectDialog(player, ConfigNodeBuilder.buildRoot(parentInstance), parentInstance, parentInstance.getClass().getSimpleName());
            });
        } else {
            InputDialogs.openTextEditor(player, parentInstance, parentInstance, field, () -> {
                showObjectDialog(player, ConfigNodeBuilder.buildRoot(parentInstance), parentInstance, parentInstance.getClass().getSimpleName());
            });
        }
    }

    private static void handleListClick(Player player, ConfigNode.ObjectNode parentNode, ConfigNode.ListNode node, Object parentInstance) throws Exception {
        List<ActionButton> buttons = new ArrayList<>();
        List<DialogBody> body = List.of(DialogBody.plainMessage(Component.text("Editing list: " + node.name())));

        Field field = findField(parentInstance, node.name());
        field.setAccessible(true);
        List<String> list = new ArrayList<>();
        Object raw = field.get(parentInstance);
        if (raw instanceof java.util.Collection<?> col) {
            for (Object o : col) list.add(o == null ? "" : o.toString());
        } else if (raw != null && raw.getClass().isArray()) {
            int len = Array.getLength(raw);
            for (int i = 0; i < len; i++) list.add(String.valueOf(Array.get(raw, i)));
        }

        for (int i = 0; i < list.size(); i++) {
            final int idx = i;
            String val = list.get(i);
            List<ActionButton> nestedButtons = new ArrayList<>();
            nestedButtons.add(ActionButton.builder(Component.text("Save")).action(DialogAction.customClick((view, aud) -> {
                try {
                    String newVal = view.getText("value");
                    list.set(idx, newVal);
                    writeListBackToField(field, parentInstance, list);
                    Object root = findRootConfigInstance(parentInstance);
                    if (root != null) ConfigManager.saveConfig(root);
                    handleListClick(player, parentNode, node, parentInstance);
                } catch (Exception e) {
                    SBLogger.err(e.getMessage());
                }
            }, ClickCallback.Options.builder().build())).build());
            nestedButtons.add(ActionButton.builder(Component.text("Cancel")).action(DialogAction.customClick((r, ab) -> {}, ClickCallback.Options.builder().build())).build());

            ActionButton itemBtn = ActionButton.builder(Component.text("#" + idx + ": " + val))
                    .action(DialogAction.customClick((v, a) -> {
                        Dialog dialog = Dialog.create(b -> b
                                .empty()
                                .base(DialogBase.builder(Component.text("Edit element " + idx))
                                        .body(List.of(DialogBody.plainMessage(Component.text("New value for index " + idx))))
                                        .inputs(List.of(io.papermc.paper.registry.data.dialog.input.DialogInput.text("value", Component.text("Value:")).width(300).labelVisible(true).initial(val).maxLength(1024).build()))
                                        .build())
                                .type(DialogType.multiAction(nestedButtons, null, 1))
                        );
                        player.showDialog(dialog);
                    }, ClickCallback.Options.builder().build()))
                    .build();

            buttons.add(itemBtn);
        }

        ActionButton add = ActionButton.builder(Component.text("Add element"))
                .action(DialogAction.customClick((v, a) -> {
                    List<ActionButton> addButtons = new ArrayList<>();
                    addButtons.add(ActionButton.builder(Component.text("Add")).action(DialogAction.customClick((view, aud) -> {
                        try {
                            String nv = view.getText("value");
                            list.add(nv);
                            writeListBackToField(field, parentInstance, list);
                            Object root = findRootConfigInstance(parentInstance);
                            if (root != null) ConfigManager.saveConfig(root);
                            handleListClick(player, parentNode, node, parentInstance);
                        } catch (Exception e) {
                            SBLogger.err(e.getMessage());
                        }
                    }, ClickCallback.Options.builder().build())).build());
                    addButtons.add(ActionButton.builder(Component.text("Cancel")).action(DialogAction.customClick((r, ab) -> {}, ClickCallback.Options.builder().build())).build());

                    Dialog dialog = Dialog.create(builder -> builder
                            .empty()
                            .base(DialogBase.builder(Component.text("Add element"))
                                    .body(List.of(DialogBody.plainMessage(Component.text("Enter new element"))))
                                    .inputs(List.of(io.papermc.paper.registry.data.dialog.input.DialogInput.text("value", Component.text("Value:")).width(300).labelVisible(true).maxLength(1024).build()))
                                    .build())
                            .type(DialogType.multiAction(addButtons, null, 1))
                    );
                    player.showDialog(dialog);
                }, ClickCallback.Options.builder().build()))
                .build();
        buttons.add(add);

        ActionButton back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.customClick((r,a) -> showObjectDialog(player, parentNode, parentInstance, parentInstance.getClass().getSimpleName()), ClickCallback.Options.builder().build()))
                .build();

        ActionButton reload = ActionButton.builder(Component.text("Reload"))
                .action(DialogAction.customClick((r, aud) -> {
                    try {
                        Class<?> rootClass = findRootConfigClass(parentInstance);
                        ConfigManager.reloadConfig(rootClass);
                        Object fresh = ConfigManager.getConfig(rootClass);
                        showObjectDialog(player, ConfigNodeBuilder.buildRoot(fresh), fresh, rootClass.getSimpleName());
                    } catch (Exception e) {
                        SBLogger.err(e.getMessage());
                        player.sendMessage(Component.text("§cFailed to reload: " + e.getMessage()));
                    }
                }, ClickCallback.Options.builder().build()))
                .build();

        List<ActionButton> finalButtons = new ArrayList<>(buttons);
        finalButtons.add(back);
        finalButtons.add(reload);

        Dialog dialog = Dialog.create(builder -> builder
                .empty()
                .base(DialogBase.builder(Component.text("List: " + node.name()))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(finalButtons, null, 1))
        );

        player.showDialog(dialog);
    }

    private static void writeListBackToField(Field field, Object parentInstance, List<String> list) throws Exception {
        Class<?> type = field.getType();
        if (java.util.Collection.class.isAssignableFrom(type)) {
            field.set(parentInstance, new ArrayList<>(list));
        } else if (type.isArray() && type.getComponentType() == String.class) {
            field.set(parentInstance, list.toArray(new String[0]));
        }
    }

    private static void handleObjectClick(Player player, ConfigNode.ObjectNode node, Object parentInstance) throws Exception {
        Field f = findField(parentInstance, node.name());
        f.setAccessible(true);
        Object nested = f.get(parentInstance);
        if (nested == null) {
            nested = node.type().getDeclaredConstructor().newInstance();
            f.set(parentInstance, nested);
        }
        showObjectDialog(player, (ConfigNode.ObjectNode) node, nested, node.name());
    }

    private static Field findField(Object parentInstance, String name) throws NoSuchFieldException {
        Class<?> c = parentInstance.getClass();
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Object findRootConfigInstance(Object instance) {
        Class<?> c = instance.getClass();
        while (c != null) {
            if (c.isAnnotationPresent(ConfigFile.class)) {
                return ConfigManager.getConfig(c);
            }
            c = c.getEnclosingClass();
        }
        for (Class<?> k : ConfigManager.getLoadedConfigs().keySet()) {
            if (ConfigManager.getConfig(k) == instance) return instance;
        }
        return null;
    }

    private static Class<?> findRootConfigClass(Object instance) {
        Class<?> c = instance.getClass();
        while (c != null) {
            if (c.isAnnotationPresent(ConfigFile.class)) return c;
            c = c.getEnclosingClass();
        }
        for (Class<?> k : ConfigManager.getLoadedConfigs().keySet()) {
            if (ConfigManager.getConfig(k) == instance) return k;
        }
        return instance.getClass();
    }

}
