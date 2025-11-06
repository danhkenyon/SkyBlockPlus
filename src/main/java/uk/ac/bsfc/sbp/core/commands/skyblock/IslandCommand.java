package uk.ac.bsfc.sbp.core.commands.skyblock;

import uk.ac.bsfc.sbp.core.commands.skyblock.subcommands.*;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.user.SBConsole;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.List;

public class IslandCommand extends SBCommand {
    public IslandCommand() {
        super();

        super.name("island");
        super.description("Main island command.");
        super.usage("/island <subcommand>");
        super.permission(null);

        super.aliases("is", "island");
    }
    @Override
    public void execute() {
        if (super.getUser() instanceof SBConsole) {
            SBLogger.err("Command cannot be ran by CONSOLE.");
        }

        assert super.getUser() instanceof SBPlayer;
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", super.getUser().getUniqueID());

        if (args.length == 0) {
            if (member == null) {
                user.sendMessage("&e/island info &7| &fView information about your island.");
                return;
            }
            member.sendMessage("&e/island create &7| &fCreate a new island.");
            return;
        }

        if (args[0].equalsIgnoreCase("help")) HelpSubcommand.execute(this);
        else if (args[0].equalsIgnoreCase("create")) CreateSubcommand.execute(this);
        else if (args[0].equalsIgnoreCase("info")) InfoSubcommand.execute(this);
        else if (args[0].equalsIgnoreCase("delete")) DeleteSubcommand.execute(this);
        else if (args[0].equalsIgnoreCase("invite")) {
            // /island invite <player> [player_rank]
            // /island invite accept <island_name>
            // /island invite deny <island_name>
            InviteSubcommand.execute(this);
            member.sendMessage("&cThe invite subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("kick")) {
            // /island kick <player>
            member.sendMessage("&cThe kick subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("spawn")) {
            // /island spawn
            member.sendMessage("&cThe home subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            // /island setspawn
            member.sendMessage("&cThe sethome subcommand is not yet implemented.");
        }
        else {
            member.sendMessage("&cUnknown subcommand. Use &e&l/island help &cfor assistance.");
        }


    }
    @Override
    public List<String> suggestions(int index) {
        return switch(index) {
            case 0 -> List.of("help", "create", "info", "delete", "invite", "kick", "spawn", "setspawn");
            default -> List.of();
        };
    }
}
