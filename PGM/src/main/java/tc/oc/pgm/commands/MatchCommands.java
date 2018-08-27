package tc.oc.pgm.commands;

import javax.inject.Inject;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import tc.oc.commons.core.commands.Commands;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.commons.core.util.TimeUtils;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.filters.matcher.match.MonostableFilter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchFormatter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.worldborder.WorldBorder;
import tc.oc.pgm.worldborder.WorldBorderMatchModule;
import tc.oc.pgm.xml.InvalidXMLException;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchCommands implements Commands {

    private final MatchFormatter formatter;

    @Inject MatchCommands(MatchFormatter formatter) {
        this.formatter = formatter;
    }

    @Command(
        aliases = {"matchinfo", "match"},
        desc = "Shows information about the current match",
        min = 0,
        max = 0
    )
    @CommandPermissions("pgm.matchinfo")
    public void matchinfo(CommandContext args, CommandSender sender) throws CommandException {
        formatter.sendMatchInfo(sender, CommandUtils.getMatch(sender));
    }

    @Command(
            aliases = {"health", "h"},
            desc = "Shows a players health",
            min = 0,
            max = 1
    )
    public void health(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getString(0).equals("*")) {
            for (MatchPlayer player : CommandUtils.getMatch(sender).getParticipatingPlayers()) {
                sender.sendMessage(player.getDisplayName(sender) + ChatColor.YELLOW + " - " + new DecimalFormat("#.##").format(player.getBukkit().getHealth()));
            }
        } else {
            Player player = tc.oc.commons.bukkit.commands.CommandUtils.getPlayerOrSelf(args, sender, 0);
            sender.sendMessage(ChatColor.YELLOW.toString() + new DecimalFormat("#.##").format(player.getHealth()));
        }
    }

    @Command(
            aliases = {"teams"},
            desc = "Displays all teams",
            min = 0,
            max = 1
    )
    public void teams(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        StringBuilder message = new StringBuilder();
        sender.sendMessage(ChatColor.AQUA + "-------------------------");
        if (args.argsLength() > 0) {
            MatchPlayer target = CommandUtils.getMatchPlayer(args, sender, 0);
            Party team = target.getParty();

            String prefix = "";
            message.append(org.bukkit.ChatColor.YELLOW).append(team.getName()).append(": ");
            for (MatchPlayer player1 : team.getPlayers()) {
                message.append(ChatColor.WHITE);
                message.append(prefix);
                prefix = ", ";
                message.append(player1.getDisplayName(player));
            }
            player.sendMessage(message.toString());
        } else {
            for (Competitor team : player.getMatch().getCompetitors()) {
                message = new StringBuilder();
                if (team.getPlayers().size() <= 0) {
                    continue;
                }

                String prefix = "";
                message.append(org.bukkit.ChatColor.YELLOW).append(team.getName()).append(": ");
                for (MatchPlayer player1 : team.getPlayers()) {
                    message.append(ChatColor.WHITE);
                    message.append(prefix);
                    prefix = ", ";
                    message.append(player1.getDisplayName(player));
                }
                player.sendMessage(message.toString());
            }
        }
    }

    Map<MatchPlayer, List<MatchPlayer>> tps = new HashMap<>();
    Map<MatchPlayer, Task> tpTasks = new HashMap<>();

    @Command(
            aliases = {"tpall", "tpa"},
            flags = "r",
            desc = "TP to all players",
            min = 0,
            max = 1
    )
    @CommandPermissions("pgm.tpall")
    public void tpall(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        if (!player.isObserving()) {
            player.sendMessage(ChatColor.RED + "You can't be playing");
            return;
        }
        int duration = args.getInteger(0, 5);
        List<MatchPlayer> players = new ArrayList<>(player.getMatch().getParticipatingPlayers());
        players.remove(player);

        if (args.hasFlag('r')) {
            Collections.reverse(players);
        }

        tps.remove(player);
        tps.put(player, players);
        if (tpTasks.containsKey(player)) {
            tpTasks.get(player).cancel();
            tpTasks.remove(player);
        }
        tpTasks.put(player, player.getMatch().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofSeconds(duration), new Runnable() {
            @Override
            public void run() {
                if (tps.get(player).size() <= 0) {
                    tpTasks.get(player).cancel();
                    tpTasks.remove(player);
                    tps.remove(player);
                    return;
                }
                MatchPlayer tp = tps.get(player).iterator().next();
                if (tp == null || !tp.isOnline()) {
                    player.sendMessage(org.bukkit.ChatColor.RED + "Teleport failed");
                    tps.get(player).remove(tp);
                    return;
                }
                player.getBukkit().teleport(tp.getBukkit().getLocation());
                player.sendMessage(org.bukkit.ChatColor.AQUA + "Teleported to " + tp.getDisplayName());
                tps.get(player).remove(tp);
            }
        }));
    }

    @Command(
            aliases = {"tppause", "tpp"},
            desc = "Pause tps",
            min = 0,
            max = 0
    )
    @CommandPermissions("pgm.tpall")
    public void tppause(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        if (!tpTasks.containsKey(player)) {
            return;
        }
        tpTasks.get(player).cancel();
        tpTasks.remove(player);
        player.sendMessage(org.bukkit.ChatColor.AQUA + "Paused");
    }

    @Command(
            aliases = {"tpcontinue", "tpc"},
            desc = "Continue tps",
            min = 0,
            max = 0
    )
    @CommandPermissions("pgm.tpall")
    public void tpcontinue(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        if (tpTasks.containsKey(player) || !tps.containsKey(player)) {
            return;
        }
        tpTasks.put(player, player.getMatch().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofSeconds(5), new Runnable() {
            @Override
            public void run() {
                if (tps.get(player).size() <= 0) {
                    tpTasks.get(player).cancel();
                    tpTasks.remove(player);
                    tps.remove(player);
                    return;
                }

                MatchPlayer tp = tps.get(player).iterator().next();
                if (tp == null || !tp.isOnline()) {
                    player.sendMessage(org.bukkit.ChatColor.RED + "Teleport failed");
                    tps.get(player).remove(tp);
                    return;
                }
                player.getBukkit().teleport(tp.getBukkit().getLocation());
                player.sendMessage(org.bukkit.ChatColor.AQUA + "Teleported to " + tp.getDisplayName());
                tps.get(player).remove(tp);
            }
        }));
    }

    @Command(
            aliases = {"tpend", "tpe"},
            desc = "Stop tps",
            min = 0,
            max = 0
    )
    @CommandPermissions("pgm.tpall")
    public void tpstop(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        player.sendMessage(org.bukkit.ChatColor.AQUA + "Stopped");
        tps.remove(player);
        if (!tpTasks.containsKey(player)) {
            return;
        }
        tpTasks.get(player).cancel();
        tpTasks.remove(player);
    }

    @Command(
            aliases = {"borders", "worldborder", "worldborders", "border"},
            desc = "Displays all teams",
            min = 0,
            max = 0
    )
    public void border(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);

        WorldBorderMatchModule borderMatchModule = player.getMatch().getMatchModule(WorldBorderMatchModule.class);
        if (borderMatchModule != null) {
//            String borderSize = borderMatchModule.getAppliedBorder() != null ? Integer.toString((int)((borderMatchModule.getAppliedBorder().getSize()) / 2)) : "\u221e"; // ∞;
            player.sendMessage(ChatColor.AQUA + "------------------------------");
            for (WorldBorder border : borderMatchModule.borders) {
                String size = Integer.toString((int)((border.getSize()) / 2));

                String time = border.after != null ? PeriodFormats.formatColonsLong(border.after).toPlainText() : "Default";
                player.sendMessage((borderMatchModule.borders.indexOf(border) + 1) + ". " + ChatColor.YELLOW + time + ChatColor.WHITE + ": (-" + size + ",-" + size + ") to (" + size + "," + size + ")");
            }
        }
    }

    @Command(
            aliases = {"removeborder"},
            desc = "Remove a border",
            usage = "<index>",
            min = 1,
            max = 1
    )
    @CommandPermissions("pgm.removeborder")
    public void removeBorder(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);
        int index = args.getInteger(0);

        WorldBorderMatchModule borderMatchModule = player.getMatch().getMatchModule(WorldBorderMatchModule.class);
        if (borderMatchModule != null) {
            if (index <= 0 || borderMatchModule.borders.size() < index) {
                player.sendMessage(ChatColor.RED + "Border not found");
                return;
            }
            borderMatchModule.borders.remove(borderMatchModule.borders.get(index - 1));
            player.sendMessage(ChatColor.YELLOW + "Border removed");
        }
    }

    @Command(
            aliases = {"addborder"},
            desc = "Add a border",
            usage = "<time> <size>",
            min = 2,
            max = 2
    )
    @CommandPermissions("pgm.addborder")
    public void addBorder(CommandContext args, CommandSender sender) throws CommandException {
        MatchPlayer player = CommandUtils.senderToMatchPlayer(sender);

        WorldBorderMatchModule borderMatchModule = player.getMatch().getMatchModule(WorldBorderMatchModule.class);
        if (borderMatchModule != null) {
            Duration after = TimeUtils.parseDuration(args.getString(0));

            WorldBorder border = new WorldBorder(
                    new Vector(0d, 0d, 0d),
                    args.getDouble(1)*2,
                    Duration.ZERO,
                    0.2d,
                    5d,
                    5d,
                    Duration.ofSeconds(15),
                 true,
                    true,
                    true,
                    after
            );

            borderMatchModule.borders.add(border);
            borderMatchModule.borders.sort((wb1, wb2) -> {
                if (wb1.after == null) {
                    return -1;
                } else if (wb2.after == null) {
                    return 1;
                }
                return wb1.after.compareTo(wb2.after);
            });
            player.sendMessage(ChatColor.YELLOW + "Border added");
        }
    }

    @Command(
            aliases = {"help"},
            desc = "UHC help"
    )
    public void help(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(ChatColor.AQUA + "-------------------------------");
        sender.sendMessage(ChatColor.GOLD + "/uhc" + ChatColor.GRAY + " (lists information about the match)");
        sender.sendMessage(ChatColor.GOLD + "/rules" + ChatColor.GRAY + " (lists all of the UHC rules)");
        sender.sendMessage(ChatColor.GOLD + "/border" + ChatColor.GRAY + " (displays time and size changes for border)");
        sender.sendMessage(ChatColor.GOLD + "/h <username>" + ChatColor.GRAY + " (/h * to display everyones health)");
        sender.sendMessage(ChatColor.GOLD + "/scenario list" + ChatColor.GRAY + " (lists all scenarios)");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "/teams" + ChatColor.GRAY + " (/teams <username> to display a specific person's team)");
        sender.sendMessage(ChatColor.GOLD + "/team invite <username>" + ChatColor.GRAY + " (send a party invite to your friend)");
        sender.sendMessage(ChatColor.GOLD + "/team accept <username>" + ChatColor.GRAY + " (accept a party invite from your friend)");
        sender.sendMessage(ChatColor.GOLD + "/team solo" + ChatColor.GRAY + " (added to the solo queue)");
        sender.sendMessage(ChatColor.GOLD + "/team soloquit" + ChatColor.GRAY + " (removed from the solo queue)");
    }

    @Command(
            aliases = {"rules"},
            desc = "UHC rules"
    )
    public void rules(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(ChatColor.AQUA + "-------------------------------");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "No strip mining or pokeholing below y: 32");
        sender.sendMessage(ChatColor.GRAY + "  - " + ChatColor.GOLD + "Digging to sounds and players is allowed");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "Rollercoastering is allowed (staircasing from y=32 to y=0 back to y=32)");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "No stalling, unless the scenarios permit it");
        sender.sendMessage(ChatColor.GRAY + "  - " + ChatColor.GOLD + "Includes but is not limited to");
        sender.sendMessage(ChatColor.GRAY + "    - " + ChatColor.GOLD + "Hiding underground once the border reaches a radius of 100 blocks");
        sender.sendMessage(ChatColor.GRAY + "    - " + ChatColor.GOLD + "Making a skybase within a radius of 100 blocks of 0,0");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "iPVP during the grace period is not allowed (lava bucketing, flint and steel, etc)");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "Cross teaming is not allowed");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "Sacrificing yourself to give your gear to another team is not allowed");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "You cannot mention coordinates or a players loots after death");
        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + "Relogging is allowed, but there is a limit to how often and how long you get disconnected.");
    }


}
