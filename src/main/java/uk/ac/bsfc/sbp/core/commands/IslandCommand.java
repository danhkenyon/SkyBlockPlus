package uk.ac.bsfc.sbp.core.commands;

import uk.ac.bsfc.sbp.core.Island;
import uk.ac.bsfc.sbp.core.Member;
import uk.ac.bsfc.sbp.core.commands.subcommands.CreateSubcommand;
import uk.ac.bsfc.sbp.core.commands.subcommands.HelpSubcommand;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
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
        Member member = Member.of((SBPlayer) super.getUser());

        if (args.length == 0) {
            if (member.getIsland() != null) {
                member.sendMessage("&e/island info &7| &fView information about your island.");
                return;
            }
            member.sendMessage("&e/island create &7| &fCreate a new island.");
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            HelpSubcommand.execute(this);
        } else if (args[0].equalsIgnoreCase("create")) {
            CreateSubcommand.execute(this);
        } else if (args[0].equalsIgnoreCase("info")) {
            Island island = member.getIsland();
            if (island == null) {
                member.sendMessage("&cYou do not have an island! Use &e&l/island create &cto make one.");
                return;
            }
            member.sendMessage("&6&l=== &e&lIsland Info &6&l===");
            member.sendMessage("&eName: &b" + island.getName());
            member.sendMessage("&eID: &b" + island.getId());
            member.sendMessage("&eLeader: &b" + island.getLeader().username());
            member.sendMessage("&eMembers: &b" + island.getMembers().size());
        } else if (args[0].equalsIgnoreCase("delete")) {
            Island island = member.getIsland();
            if (island == null) {
                member.sendMessage("&cYou do not have an island to delete!");
                return;
            }
            if (!island.getLeader().uuid().equals(member.uuid())) {
                member.sendMessage("&cOnly the island leader can delete the island.");
                return;
            }
            // TODO: Add deletion code.
            member.sendMessage("&aSuccessfully deleted your island.");
        } else if (args[0].equalsIgnoreCase("invite")) {
            // /island invite <player> [player_rank]
            // /island invite <island_name> accept
            // /island invite <island_name> deny
            member.sendMessage("&cThe invite subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("kick")) {
            // /island kick <player>
            member.sendMessage("&cThe kick subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            // /island setspawn
            member.sendMessage("&cThe sethome subcommand is not yet implemented.");
        } else if (args[0].equalsIgnoreCase("spawn")) {
            // /island spawn
            member.sendMessage("&cThe home subcommand is not yet implemented.");
        }
        else {
            member.sendMessage("&cUnknown subcommand. Use &e&l/island help &cfor assistance.");
        }


    }
    @Override
    public List<String> suggestions(int index) {
        return List.of();
    }
}
