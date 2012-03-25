package com.palmergames.bukkit.towny.command;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.question.AbstractQuestion;
import com.palmergames.bukkit.towny.question.Option;
import com.palmergames.bukkit.towny.question.Question;
import com.palmergames.bukkit.towny.question.QuestionTask;
import com.palmergames.util.StringMgmt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Q command for questioner.
 */
public class QCommand implements CommandExecutor {
    private final Towny plugin;

    private List<Option> currentOptions = new ArrayList<Option>();

    private List<String> currentTargets = new ArrayList<String>();

    public QCommand(Towny plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String command = cmd.getName().toLowerCase();
        if (command.equals("q")) {
            if (args.length > 0) {
                if (sender.isOp()) {
                    if (args[0].equalsIgnoreCase("target")) {
                        for (int i = 1; i < args.length; i++) {
                            currentTargets.add(args[i]);
                        }
                        sender.sendMessage("NumTargets: " + currentTargets.size());
                        return true;
                    } else if (args[0].equalsIgnoreCase("opt")) {
                        if (args.length > 1) {
                            currentOptions.add(new Option(args[1], new QuestionTask() {
                                public void run() {
                                    System.out.println("You chose " + getOption().getOptionString() + "!");
                                }

                            }));
                            sender.sendMessage("NumOptions: " + currentOptions.size());
                        } else {
                            sender.sendMessage("help > que opt [option]");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("ask")) {
                        try {
                            String q = StringMgmt.join(StringMgmt.remFirstArg(args), " ");
                            for (String target : currentTargets) {
                                Question question = new Question(target, q, currentOptions);
                                plugin.getQuestionManager().appendQuestion(question);
                                Player player = Bukkit.getPlayer(target);
                                if (player != null) {
                                    player.sendMessage(q);
                                }
                            }
                            currentOptions.clear();
                            currentTargets.clear();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        int page = 1;
                        if (args.length > 1) {
                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                            }
                        }
                        for (String line : plugin.getQuestionManager().formatQuestionList(player.getName(), page)) {
                            player.sendMessage(line);
                        }
                        return true;
                    }
                }
            }

            sender.sendMessage("Invalid sub command.");
            return true;
        }

        return false;
    }

}
