package uk.ac.bsfc.sbp.core.commands.general;

import net.minecraft.server.level.ServerPlayer;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.npc.NPC;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class TestCommand extends SBCommand {
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
        ServerPlayer sPlayer = user.to(SBPlayer.class).getServerPlayer();

        new NPC(sPlayer.level(), "Dummy", sPlayer.getX(), sPlayer.getY(), sPlayer.getZ()).spawn();
    }
}

