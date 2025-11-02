package uk.ac.bsfc.sbp.core.commands.general;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.game.SBServer;

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
        SBServer.broadcast(super.getUser(), "&aTest command executed!");
    }
}

