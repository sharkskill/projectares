package tc.oc.pgm.teams;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.minecraft.util.commands.SuggestException;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.commons.bukkit.localization.Translations;
import tc.oc.commons.core.commands.Commands;
import tc.oc.commons.core.commands.NestedCommands;
import tc.oc.commons.core.commands.TranslatableCommandException;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.commands.CommandUtils;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.inject.MatchScoped;

@MatchScoped
public class TeamCommands implements NestedCommands {

    @Singleton
    public static class Parent implements Commands {
        @Command(
                aliases = {"team"},
                desc = "Commands for working with teams",
                min = 1,
                max = -1
        )
        @NestedCommand({TeamCommands.class})
        public void team() {}
    }

    private final TeamCommandUtils utils;
    private final Set<Team> teams;

    @Inject TeamCommands(TeamCommandUtils utils, Set<Team> teams) {
        this.utils = utils;
        this.teams = teams;
    }

    @Command(
            aliases = {"myteam", "mt"},
            desc = "Shows you what team you are on",
            min = 0,
            max = 0
    )
    @CommandPermissions("pgm.myteam")
    public void myteam(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        if(player.getParty() instanceof Team) {
            sender.sendMessage(ChatColor.GRAY + PGMTranslations.t("command.gameplay.myteam.message", player, player.getParty().getColoredName() + ChatColor.GRAY));
        } else {
            throw new CommandException(PGMTranslations.get().t("command.gameplay.myteam.notOnTeam", sender));
        }
    }

    @Command(
            aliases = {"force"},
            desc = "Force a player onto a team",
            usage = "<player> [team]",
            min = 1,
            max = 2
    )
    @CommandPermissions("pgm.team.force")
    public void force(CommandContext args, CommandSender sender) throws CommandException, SuggestException {
        MatchPlayer player = CommandUtils.findSingleMatchPlayer(args, sender, 0);

        if(args.argsLength() >= 2) {
            String name = args.getString(1);
            if(name.trim().toLowerCase().startsWith("obs")) {
                player.getMatch().setPlayerParty(player, player.getMatch().getDefaultParty(), false);
            } else {
                Team team = utils.teamArgument(args, 1);
                utils.module().forceJoin(player, team);
            }
        } else {
            utils.module().forceJoin(player, null);
        }
    }

    @Command(
            aliases = {"invite"},
            desc = "Invite a player to your team",
            usage = "<player>",
            min = 1,
            max = 1
    )
    public void invite(CommandContext args, CommandSender sender) throws CommandException, SuggestException {
        MatchPlayer inviter = CommandUtils.senderToMatchPlayer(sender);
        MatchPlayer invitee = CommandUtils.findSingleMatchPlayer(args, sender, 0);

        String name = inviter.getParty().getName();
        if(name.trim().toLowerCase().startsWith("obs")) {
            throw new CommandException(PGMTranslations.get().t("command.team.invite.obs", sender));
        } else {
            Team team = utils.teamArgument(args, 1);
//            utils.module().forceJoin(player, team);
        }
    }

    @Command(
            aliases = {"accept"},
            desc = "Accept an invitation to join a team",
            usage = "<player>",
            min = 1,
            max = 1
    )
    public void accept(CommandContext args, CommandSender sender) throws CommandException, SuggestException {
        MatchPlayer inviter = CommandUtils.senderToMatchPlayer(sender);
        MatchPlayer invitee = CommandUtils.findSingleMatchPlayer(args, sender, 0);

        List<String> strings = new ArrayList<>();
        strings.add("minecraft:tp -348.5 100 403.5");
        strings.add("minecraft:tp -1049.5 100 -590.5");
        strings.add("minecraft:tp 536.5 100 575.5");
        strings.add("minecraft:tp -216.5 100 -26.5");
        strings.add("minecraft:tp -999.5 100 1000.5");
        strings.add("minecraft:tp 1450.5 100 -370.5");
        strings.add("minecraft:tp 223.5 100 -1246.5");
        strings.add("minecraft:tp 1001.5 100 -339.5");
        strings.add("minecraft:tp -548.5 100 1450.5");
        strings.add("minecraft:tp 352.5 100 1450.5");
        strings.add("minecraft:tp 691.5 100 -1377.5");
        strings.add("minecraft:tp -724.5 100 154.5");
        strings.add("minecraft:tp 224.5 100 1002.5");
        strings.add("minecraft:tp 1378.5 100 467.5");
        strings.add("minecraft:tp -787.5 100 -1149.5");
        strings.add("minecraft:tp 284.5 100 185.5");
        strings.add("minecraft:tp 675.5 100 1008.5");
        strings.add("minecraft:tp -1449.5 100 -374.5");
        strings.add("minecraft:tp -621.5 100 -284.5");
        strings.add("minecraft:tp 982.5 100 -789.5");
        strings.add("minecraft:tp 65.5 100 579.5");
        strings.add("minecraft:tp 569.5 100 -467.5");
        strings.add("minecraft:tp -207.5 100 -930.5");
        strings.add("minecraft:tp -451.5 100 -1449.5");
        strings.add("minecraft:tp 1073.5 100 798.5");
        strings.add("minecraft:tp -1449.5 100 999.5");
        strings.add("minecraft:tp 1348.5 100 -1052.5");
        strings.add("minecraft:tp -226.5 100 1003.5");
        strings.add("minecraft:tp 1253.5 100 34.5");
        strings.add("minecraft:tp 1135.5 100 -1449.5");
        strings.add("minecraft:tp 1253.5 100 1450.5");
        strings.add("minecraft:tp -97.5 100 1450.5");
        strings.add("minecraft:tp 687.5 100 -16.5");
        strings.add("minecraft:tp -1065.5 100 -140.5");
        strings.add("minecraft:tp -1449.5 100 549.5");
        strings.add("minecraft:tp -1155.5 100 -1410.5");
        strings.add("minecraft:tp -1449.5 100 99.5");
        strings.add("minecraft:tp -611.5 100 770.5");
        strings.add("minecraft:tp 173.5 100 -251.5");
        strings.add("minecraft:tp 932.5 100 360.5");
        strings.add("minecraft:tp -613.5 100 -734.5");
        strings.add("minecraft:tp -214.5 100 -478.5");
        strings.add("minecraft:tp 1450.5 100 1045.5");
        strings.add("minecraft:tp -1449.5 100 1450.5");
        strings.add("minecraft:tp 802.5 100 1450.5");
        strings.add("minecraft:tp -1420.5 100 -845.5");
        strings.add("minecraft:tp 180.5 100 -701.5");
        strings.add("minecraft:tp 559.5 100 -946.5");
        strings.add("minecraft:tp -999.5 100 541.5");
        strings.add("minecraft:tp -998.5 100 1450.5");
        Duration duration = Duration.ofSeconds(1);

        for (String str : strings) {
            inviter.getMatch().getScheduler(MatchScope.RUNNING).createDelayedTask(duration, new Runnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage(str);
                    inviter.getBukkit().performCommand(str);
                }
            });
            duration = duration.plus(Duration.ofSeconds(1));
        }
        inviter.getBukkit().performCommand("minecraft:tp ");
        String name = inviter.getParty().getName();
        if(name.trim().toLowerCase().startsWith("obs")) {
            throw new CommandException(PGMTranslations.get().t("command.team.invite.obs", sender));
        } else {
            Team team = utils.teamArgument(args, 1);
//            utils.module().forceJoin(player, team);
        }
    }

    @Command(
            aliases = {"shuffle"},
            desc = "Shuffle the teams",
            min = 0,
            max = 0
    )
    @CommandPermissions("pgm.team.shuffle")
    public void shuffle(CommandContext args, CommandSender sender) throws CommandException {
        TeamMatchModule tmm = utils.module();
        Match match = tmm.getMatch();

        if(match.isRunning()) {
            throw new CommandException(Translations.get().t("command.team.shuffle.matchRunning", sender));
        } else {
            List<Team> teams = new ArrayList<>(this.teams);
            List<MatchPlayer> participating = new ArrayList<>(match.getParticipatingPlayers());
            Collections.shuffle(participating);
            for(int i = 0; i < participating.size(); i++) {
                tmm.forceJoin(participating.get(i), teams.get((i * teams.size()) / participating.size()));
            }
            match.sendMessage(new TranslatableComponent("command.team.shuffle.success"));
        }
    }

    @Command(
            aliases = {"alias"},
            desc = "Rename a team",
            usage = "<old name> <new name>",
            min = 2,
            max = -1
    )
    @CommandPermissions("pgm.team.alias")
    public void alias(CommandContext args, CommandSender sender) throws CommandException, SuggestException {
        TeamMatchModule tmm = utils.module();
        Match match = tmm.getMatch();
        Team team = utils.teamArgument(args, 0);

        String newName = args.getJoinedStrings(1);

        if(newName.length() > 32) {
            throw new CommandException("Team name cannot be longer than 32 characters");
        }

        if(teams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(newName))) {
            throw new TranslatableCommandException("command.team.alias.nameAlreadyUsed", newName);
        }

        String oldName = team.getColoredName();
        team.setName(newName);

        match.sendMessage(oldName + ChatColor.GRAY + " renamed to " + team.getColoredName());
    }

    @Command(
            aliases = {"max", "size"},
            desc = "Change the maximum size of a team. If max-overfill is not specified, it will be the same as max-players.",
            usage = "<team> (default | <max-players> [max-overfill])",
            min = 2,
            max = 3
    )
    @CommandPermissions("pgm.team.size")
    public void max(CommandContext args, CommandSender sender) throws CommandException, SuggestException {

        int maxPlayers = args.getInteger(1);
        if(maxPlayers < 0) throw new CommandException("max-players cannot be less than 0");

        Integer maxOverfill = null;
        if(args.argsLength() == 3) {
            maxOverfill = args.getInteger(2);
            if (maxOverfill < maxPlayers) throw new CommandException("max-overfill cannot be less than max-players");
        }

        List<Team> teams = new ArrayList<>();

        if (args.getString(0).equals("*")) {
            List<String> teamNames = utils.teamNames();
            for (String teamName: teamNames) {
                if (!teamName.toLowerCase().contains("obs")) {
                    teams.add(utils.team(teamName));
                }
            }
        } else {
            teams.add(utils.teamArgument(args, 0));
        }

        for (Team team: teams) {
            if("default".equals(args.getString(1))) {
                team.resetMaxSize();
            } else {
                team.setMaxSize(maxPlayers, maxOverfill != null ? maxOverfill : maxPlayers);
            }
        }

        if (teams.size() == 1) {
            sender.sendMessage(teams.get(0).getColoredName() +
                    ChatColor.WHITE + " now has max size " + ChatColor.AQUA + teams.get(0).getMaxPlayers() +
                    ChatColor.WHITE + " and max overfill " + ChatColor.AQUA + teams.get(0).getMaxOverfill());
        } else if (teams.size() > 1) {
            sender.sendMessage("All teams" +
                    ChatColor.WHITE + " now have max size " + ChatColor.AQUA + teams.get(0).getMaxPlayers() +
                    ChatColor.WHITE + " and max overfill " + ChatColor.AQUA + teams.get(0).getMaxOverfill());
        }
    }

    @Command(
            aliases = {"min"},
            desc = "Change the minimum size of a team.",
            usage = "<team> (default | <min-players>)",
            min = 2,
            max = 2
    )
    @CommandPermissions("pgm.team.size")
    public void min(CommandContext args, CommandSender sender) throws CommandException, SuggestException {
        Team team = utils.teamArgument(args, 0);

        if("default".equals(args.getString(1))) {
            team.resetMinSize();
        } else {
            int minPlayers = args.getInteger(1);
            if(minPlayers < 0) throw new CommandException("min-players cannot be less than 0");
            team.setMinSize(minPlayers);
        }

        sender.sendMessage(team.getColoredName() +
                ChatColor.WHITE + " now has min size " + ChatColor.AQUA + team.getMinPlayers());
    }
}
