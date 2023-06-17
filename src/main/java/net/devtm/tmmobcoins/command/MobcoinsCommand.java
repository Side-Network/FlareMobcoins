package net.devtm.tmmobcoins.command;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.devtm.tmmobcoins.util.Utils;
import net.tmmobcoins.lib.base.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MobcoinsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            help(commandSender);
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "reload":
                    if (!commandSender.hasPermission("tmmobcoins.command.reload")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }
                    FilesManager.ACCESS.reload();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.success").prefix().toStringColor());
                    commandSender.sendMessage(MessageHandler.message("commands.reload.commands").prefix().toStringColor());
                    TMMobCoins.PLUGIN.startStorage();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.mysql").prefix().toStringColor());
                    break;
                case "set":
                    if (!commandSender.hasPermission("tmmobcoins.command.set")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }
                    if (args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);

                            ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).setMobcoins(amount);
                            String formattedAmount = ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).getFormattedMobcoins();

                            commandSender.sendMessage(MessageHandler.message("commands.set.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1]))
                                    .replace("%pl_mobcoins%", formattedAmount)
                                    .placeholderAPI(commandSender).toStringColor());
                        } catch (Exception e) {
                            e.printStackTrace();
                            commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                        }
                    } else {
                        commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                    }
                    break;
                case "remove":
                    if (!commandSender.hasPermission("tmmobcoins.command.remove")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }
                    if (args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);

                            ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).removeMobcoins(amount);
                            String formattedAmount = ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).getFormattedMobcoins();

                            commandSender.sendMessage(MessageHandler.message("commands.remove.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1]))
                                    .replace("%pl_mobcoins%", formattedAmount)
                                    .placeholderAPI(commandSender).toStringColor());
                        } catch (Exception e) {
                            e.printStackTrace();
                            commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                        }
                    } else {
                        commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                    }
                    break;
                case "give":
                    if (!commandSender.hasPermission("tmmobcoins.command.give")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }
                    if (args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);

                            ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).giveMobcoins(amount, true);
                            String formattedAmount = ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).getFormattedMobcoins();

                            commandSender.sendMessage(MessageHandler.message("commands.give.success").prefix()
                                    .replace("%pl_player%", Utils.UTILS.getPlayerName(args[1]))
                                    .replace("%pl_mobcoins%", formattedAmount)
                                    .placeholderAPI(commandSender).toStringColor());
                        } catch (Exception e) {
                            e.printStackTrace();
                            commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                        }
                    } else{
                        commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                    }
                    break;
                case "multiplier":
                    /*                 0        1      2       3 */
                    /* /tmmobcoins multiplier give [player] [value] */
                    /* /tmmobcoins multiplier reset [player] */
                    if (!commandSender.hasPermission("tmmobcoins.command.multiplier")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "set":
                            try {
                                double multiplier = Double.parseDouble(args[3]);
                                UUID uuid = Utils.UTILS.getPlayerUUID(args[2]);
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.otherplayer").prefix()
                                        .replace("%pl_player%", args[2])
                                        .replace("%pl_multiplier%", multiplier + "")
                                        .placeholderAPI(commandSender).toStringColor());

                                ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).setMultiplier(multiplier);

                                if (!args[2].equalsIgnoreCase(commandSender.getName()) && Utils.UTILS.getPlayer(args[2]) instanceof Player) {
                                    Bukkit.getPlayer(args[2]).sendMessage(MessageHandler.message("commands.multiplier.set.player").prefix()
                                            .replace("%pl_player%", args[2])
                                            .replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.help").prefix().toStringColor());
                            }
                            break;
                        case "reset":
                            try {
                                double multiplier = 1.0;
                                UUID uuid = Utils.UTILS.getPlayerUUID(args[1]);
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.player").prefix()
                                        .replace("%pl_player%", "All Server")
                                        .replace("%pl_multiplier%", multiplier + "")
                                        .placeholderAPI(commandSender).toStringColor());

                                ServiceHandler.SERVICE.getDataService().wrapPlayer(uuid).setMultiplier(multiplier);

                                if (!args[2].equalsIgnoreCase(commandSender.getName()) && Utils.UTILS.getPlayer(args[2]) instanceof Player) {
                                    Bukkit.getPlayer(args[2]).sendMessage(MessageHandler.message("commands.multiplier.set.otherplayer").prefix()
                                            .replace("%pl_player%", "All Server")
                                            .replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.help").prefix().toStringColor());
                            }
                            break;
                        case "global":
                            try {
                                double multiplier = Double.parseDouble(args[2]);
                                if (multiplier != 0) {
                                    FilesManager.ACCESS.getData().getConfig().set("global_multiplier", multiplier);
                                    FilesManager.ACCESS.getData().saveConfig();
                                    commandSender.sendMessage(MessageHandler.message("commands.multiplier.global.success").prefix()
                                            .replace("%pl_player%", "All Server")
                                            .replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                                } else {
                                    commandSender.sendMessage(MessageHandler.message("commands.multiplier.errors.cant_be").prefix().placeholderAPI(commandSender).toStringColor());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.global.help").prefix().toStringColor());
                            }
                            break;
                    }
                    break;
                case "balance":
                    if (!commandSender.hasPermission("tmmobcoins.command.balance")) {
                        commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor());
                        return true;
                    }

                    try {
                        if (args.length == 1) {
                            String formattedAmount = ServiceHandler.SERVICE.getDataService().wrapPlayer(((Player)commandSender).getUniqueId()).getFormattedMobcoins();
                            commandSender.sendMessage(MessageHandler.message("commands.balance.player").prefix()
                                    .replace("%pl_player%", commandSender.getName())
                                    .replace("%pl_mobcoins%", formattedAmount)
                                    .placeholderAPI(commandSender).toStringColor());
                        } else {
                            String formattedAmount = ServiceHandler.SERVICE.getDataService().wrapPlayer(Utils.UTILS.getPlayerUUID(args[1])).getFormattedMobcoins();
                            commandSender.sendMessage(MessageHandler.message("commands.balance.otherplayer").prefix()
                                    .replace("%pl_player%", args[1])
                                    .replace("%pl_mobcoins%", formattedAmount)
                                    .placeholderAPI(commandSender).toStringColor());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        commandSender.sendMessage(MessageHandler.message("commands.balance.help").prefix().toStringColor());
                    }
                    break;
                case "help":
                    help(commandSender);
                    break;
            }
        }

        return false;
    }

    private void help(CommandSender commandSender) {
        if (!commandSender.hasPermission("tmmobcoins.command.help")) {
            commandSender.sendMessage(MessageHandler.chat("\n  &7(v" + TMMobCoins.PLUGIN.getPlugin().getDescription().getVersion() + ")").toStringColor());
            commandSender.sendMessage(MessageHandler.message("basic.no_permission").replace("%pl_prefix%", "").toStringColor());
            commandSender.sendMessage("\n");
        } else {
            commandSender.sendMessage(MessageHandler
                    .chat("\n  <GRADIENT:#FFC837-#FFC837>TMMobcoins</GRADIENT> &7(v" + TMMobCoins.PLUGIN.getPlugin().getDescription().getVersion() + ")\n \n  &f&nArguments&f: &7[] Required, () Optional." +
                            "\n \n  &#f7971e▸ &7/tmobcoins give [player] [amount]\n  &#f9a815▸ &7/tmobcoins set [player] [amount]" +
                            "\n  &#fab011▸ &7/tmobcoins remove [player] [amount]\n  &#fcb90d▸ &7/tmobcoins balance (player)\n  &#fdc109▸ &7/tmobcoins reload <files/database/all> " +
                            "\n \n  &#17F7C1▸ &7/tmobcoins check &#17f7c1[L&#17f6c4e&#16f5c6a&#16f4c9r&#16f4cbn &#16f3cem&#15f2d1o&#15f1d3r&#15f0d6e &#14efd8a&#14eedbb&#14edddo&#14ede0u&#13ece3t &#13ebe5t&#13eae8h&#12e9eai&#12e8eds &#12e7f0p&#11e6f2l&#11e6f5u&#11e5f7g&#11e4fai&#10e3fcn&#10e2ff]\n")
                    .toStringColor());

            commandSender.sendMessage(MessageHandler.chat("\n\n&7&oNote: This plugin is still in the beta stage, if any bugs please report them on our discord server or direct message me on discord (MaikyDev#5343) or make a issues on github! You can find those links on our website!").toStringColor());
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                for (String s : Arrays.asList("give", "set", "remove", "balance", "help", "multiplier"))
                    if (sender.hasPermission("tmmobcoins.command." + s))
                        completions.add(s);
                break;
            case 2:
                if (args[0].equalsIgnoreCase("multiplier"))
                    completions.addAll(Arrays.asList("set", "reset", "global"));
                else if (!args[0].equalsIgnoreCase("help"))
                    Bukkit.getOnlinePlayers().forEach(pl -> completions.add(pl.getName()));
                break;
            case 3:
                if (args[0].equalsIgnoreCase("multiplier"))
                    if (args[1].equalsIgnoreCase("global"))
                        completions.add("[amount]");
                    else
                        Bukkit.getOnlinePlayers().forEach(pl -> completions.add(pl.getName()));
                else if (!args[0].equalsIgnoreCase("help"))
                    completions.add("[amount]");
            case 4:
                if (args[0].equalsIgnoreCase("multiplier"))
                    completions.add("[amount]");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}