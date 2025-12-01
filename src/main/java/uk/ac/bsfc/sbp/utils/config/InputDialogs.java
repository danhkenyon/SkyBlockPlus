package uk.ac.bsfc.sbp.utils.config;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class InputDialogs {

    private InputDialogs() {}

    public static void openTextEditor(Player player, Object rootConfig, Object instance, Field field, Runnable onDone) {
        String title = "Edit " + field.getName();

        TextDialogInput text = DialogInput.text("value", Component.text("New value:"))
                .width(300)
                .labelVisible(true)
                .initial(getFieldAsString(field, instance))
                .maxLength(1024)
                .build();

        ActionButton save = ActionButton.builder(Component.text("Save"))
                .action(DialogAction.customClick((view, audience) -> {
                    try {
                        String newVal = view.getText("value");
                        applyValue(field, instance, newVal);
                        uk.ac.bsfc.sbp.utils.config.ConfigManager.saveConfig(rootConfig);
                        onDone.run();
                    } catch (Exception e) {
                        player.sendMessage(Component.text("Invalid value: " + e.getMessage()));
                        SBLogger.err(e.getMessage());
                    }
                }, ClickCallback.Options.builder().build()))
                .build();

        ActionButton cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.customClick((v, a) -> {}, ClickCallback.Options.builder().build()))
                .build();

        Dialog dialog = Dialog.create(builder -> builder
                .empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(List.of(DialogBody.plainMessage(Component.text("Enter a new value for: " + field.getName()))))
                        .inputs(List.of((DialogInput) text))
                        .build())
                .type(DialogType.multiAction(List.of(save), cancel, 1))
        );

        player.showDialog(dialog);
    }

    public static void openNumberEditor(Player player, Object rootConfig, Object instance, Field field, double min, double max, Runnable onDone) {
        String title = "Edit " + field.getName();

        NumberRangeDialogInput number = DialogInput.numberRange("value", Component.text("Value:"), (float)min, (float)max)
                .width(300)
                .build();

        ActionButton save = ActionButton.builder(Component.text("Save"))
                .action(DialogAction.customClick((view, audience) -> {
                    try {
                        float f = view.getFloat("value");
                        String raw = Float.toString(f);
                        applyValue(field, instance, raw);
                        uk.ac.bsfc.sbp.utils.config.ConfigManager.saveConfig(rootConfig);
                        onDone.run();
                    } catch (Exception e) {
                        player.sendMessage(Component.text("Invalid number: " + e.getMessage()));
                        SBLogger.err(e.getMessage());
                    }
                }, ClickCallback.Options.builder().build()))
                .build();

        ActionButton cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.customClick((v, a) -> {}, ClickCallback.Options.builder().build()))
                .build();

        Dialog dialog = Dialog.create(builder -> builder
                .empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(List.of(DialogBody.plainMessage(Component.text("Set a value for: " + field.getName()))))
                        .inputs(List.of((DialogInput) number))
                        .build())
                .type(DialogType.multiAction(List.of(save), cancel, 1))
        );

        player.showDialog(dialog);
    }

    private static String getFieldAsString(Field field, Object instance) {
        try {
            Object val = field.get(instance);
            return val == null ? "" : String.valueOf(val);
        } catch (IllegalAccessException e) {
            return "";
        }
    }

    private static void applyValue(Field field, Object instance, String raw) throws Exception {
        Class<?> type = field.getType();
        if (type == int.class || type == Integer.class) field.set(instance, Integer.parseInt(raw));
        else if (type == double.class || type == Double.class) field.set(instance, Double.parseDouble(raw));
        else if (type == long.class || type == Long.class) field.set(instance, Long.parseLong(raw));
        else if (type == float.class || type == Float.class) field.set(instance, Float.parseFloat(raw));
        else if (type == String.class) field.set(instance, raw);
        else if (type.isEnum()) {
            @SuppressWarnings({"unchecked","rawtypes"})
            Object enumVal = java.lang.Enum.valueOf((Class) type, raw);
            field.set(instance, enumVal);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
    }
}