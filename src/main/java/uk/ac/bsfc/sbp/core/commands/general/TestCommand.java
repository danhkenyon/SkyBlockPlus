package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.SBFlatWorld;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;
import uk.ac.bsfc.sbp.utils.time.SBTime;

@ApiStatus.Experimental
public class  TestCommand extends SBCommand {
    MiniMessage mm = MiniMessage.miniMessage();

    public TestCommand() {
        super();
        super.name("test");
        super.description("A test command.");
        super.usage("/test");
        super.permission(null);

        super.aliases("test-command");
    }
    @Override
    public void execute() {
        SBFlatWorld world = SBFlatWorld.create(SBTime.now().millis()+"-world", WorldEnvironment.NORMAL, 0,
                new FlatWorldGenerator.Layer(org.bukkit.Material.BEDROCK, 1),
                new FlatWorldGenerator.Layer(org.bukkit.Material.DIRT, 2),
                new FlatWorldGenerator.Layer(org.bukkit.Material.GRASS_BLOCK, 1)
        );

        world.toBukkit();
    }
}

