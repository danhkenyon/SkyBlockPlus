package uk.ac.bsfc.sbp.core.commands.config;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.config.ConfigEditorUI;
import uk.ac.bsfc.sbp.utils.config.ConfigManager;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public class EditConfigCommand extends SBCommand {
    public EditConfigCommand() {
        super();
        this.name("edit-config");
        this.description("Used to edit a config file.");
        this.permission("sbp.config.edit-config");
    }

    @Override
    public void execute() {
        if (args().length != 0) {
            user.sendMessage("<red>Usage: /edit-config");
            return;
        }

        if (!(user instanceof SBPlayer sbPlayer)) {
            user.sendMessage("<red>This command can only be used by players.");
            return;
        }

        Player player = sbPlayer.toBukkit(Player.class);

        List<ActionButton> buttons = new ArrayList<>();

        for (Map.Entry<Class<?>, Object> entry : ConfigManager.getLoadedConfigs().entrySet()) {
            Class<?> clazz = entry.getKey();
            String displayName = clazz.isAnnotationPresent(uk.ac.bsfc.sbp.utils.config.ConfigFile.class)
                    ? clazz.getAnnotation(uk.ac.bsfc.sbp.utils.config.ConfigFile.class).value()
                    : clazz.getSimpleName();

            ActionButton btn = ActionButton.builder(Component.text(displayName))
                    .action(DialogAction.customClick((view, aud) -> {
                        ConfigEditorUI.openRootEditor(player, clazz);
                    }, ClickCallback.Options.builder().build()))
                    .build();

            buttons.add(btn);
        }

        ActionButton exit = ActionButton.builder(Component.text("Exit"))
                .action(DialogAction.customClick((r, aud) -> {}, ClickCallback.Options.builder().build()))
                .build();

        Dialog dialog = Dialog.create(builder -> builder
                .empty()
                .base(DialogBase.builder(Component.text("Config Editor"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Select a config to edit:"))))
                        .build())
                .type(DialogType.multiAction(buttons, exit, 1))
        );

        player.showDialog(dialog);
    }
}
