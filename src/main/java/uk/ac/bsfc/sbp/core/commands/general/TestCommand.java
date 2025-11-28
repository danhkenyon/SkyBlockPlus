package uk.ac.bsfc.sbp.core.commands.general;

import org.jetbrains.annotations.ApiStatus;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.IslandTable;

@ApiStatus.Experimental
public class  TestCommand extends SBCommand {
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
        /*
        SBFlatWorld world = SBFlatWorld.create(SBTime.now().millis()+"-world", WorldEnvironment.NORMAL, 696969696969696969L,
                new FlatWorldGenerator.Layer(Material.TNT, 1),
                new FlatWorldGenerator.Layer(Material.SAND, 1),
                new FlatWorldGenerator.Layer(Material.STONE_PRESSURE_PLATE, 1)
        ).generateCaves(true) // doesnt work
        .generateStructures(true); // also doesnt work

        world.toBukkit();

         */

        user.sendMessage(IslandTable.getInstance().getLastIslandLocation().toString());
        user.sendMessage(IslandTable.getInstance().getNextIslandLocation().toString());
    }
}

