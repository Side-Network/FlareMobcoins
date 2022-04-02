package net.devtm.tmmobcoins.command;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.util.StorageAccess;
import net.devtm.tmmobcoins.util.MobCoinsPlayer;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.base.VersionCheckers;
import net.tmmobcoins.lib.menu.Menu;
import net.tmmobcoins.lib.menu.item.ItemHandler;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.*;

public class MobcoinsCommand extends BukkitCommand {

    public MobcoinsCommand(String cmdName, String description, List<String> aliases) {
        super(cmdName);
        this.setDescription(description);
        this.setUsage(cmdName);
        this.setAliases(aliases);
    }

    @Override
    public List<String> tabComplete(CommandSender sender , String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> a = new ArrayList<>();
                for(String s : Arrays.asList("give", "set", "remove", "balance", "help", "multiplier"))
                    if(sender.hasPermission("tmmobcoins.command." + s)) a.add(s);
                return a;
            case 2:
                if(args[0].equalsIgnoreCase("multiplier")) {
                   return Arrays.asList("set", "reset", "global");
                }
                if(!args[0].equalsIgnoreCase("help")) {
                    List<String> list = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(pl -> list.add(pl.getName()));
                    return list;
                }
            case 3:
                if(args[0].equalsIgnoreCase("multiplier")) {
                    if(args[1].equalsIgnoreCase("global")) {
                        return Collections.singletonList("[amount]");
                    } else {
                        List<String> list = new ArrayList<>();
                        Bukkit.getOnlinePlayers().forEach(pl -> list.add(pl.getName()));
                        return list;
                    }
                }
                if(!args[0].equalsIgnoreCase("help")) {
                    return Collections.singletonList("[amount]");
                }
            case 4:
                if(args[0].equalsIgnoreCase("multiplier")) {
                    return Collections.singletonList("[amount]");
                }
        }
        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(args.length < 1) {
            if(TMMobCoins.PLUGIN.isEnabledMenu()
                    && (commandSender instanceof Player) && commandSender.hasPermission("tmmobcoins.shop")
                    && FilesManager.FILES.getConfig().getConfig().getBoolean("shop.settings.default_command")) {

                Configuration config = FilesManager.FILES.getConfig().getConfig();
                Menu menu = new Menu((Player) commandSender, MessageHandler.chat(config.getString("shop.menu_title")).placeholderAPI(commandSender).toStringColor(), config.getInt("shop.size"));
                for (String s1 : config.getConfigurationSection("shop.items").getKeys(false))
                    menu.assignItems(new ItemHandler().setPlayer(((Player) commandSender).getPlayer()).autoGetter(config, "shop.items", s1));
                menu.updateInventory();
                ((Player) commandSender).openInventory(menu.inventory);
            } else {
                help(commandSender);
            }
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "reload":
                    if(!commandSender.hasPermission("tmmobcoins.command.reload")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    FilesManager.FILES.reload();
                    //TMTokens.PLUGIN.commandsSetup();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.success").prefix().toStringColor());
                    commandSender.sendMessage(MessageHandler.message("commands.reload.commands").prefix().toStringColor());
                    TMMobCoins.PLUGIN.startStorage();
                    commandSender.sendMessage(MessageHandler.message("commands.reload.mysql").prefix().toStringColor());
                    break;
                case "set":
                    if(!commandSender.hasPermission("tmmobcoins.command.set")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            MobCoinsPlayer tp = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[1]));
                            commandSender.sendMessage(MessageHandler.message("commands.set.success").prefix()
                                    .replace("%pl_player%", TMMobCoins.PLUGIN.getUtils().getPlayerName(args[1])).replace("%pl_mobcoins%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && TMMobCoins.PLUGIN.getUtils().getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.set.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_mobcoins%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                            tp.setMobcoins(amount); tp.uploadPlayer();
                        } catch (Exception e) {
                            e.printStackTrace();
                            commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.set.help").prefix().toStringColor());
                    break;
                case "remove":
                    if(!commandSender.hasPermission("tmmobcoins.command.remove")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            MobCoinsPlayer tp = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[1]));
                            commandSender.sendMessage(MessageHandler.message("commands.remove.success").prefix()
                                    .replace("%pl_player%", TMMobCoins.PLUGIN.getUtils().getPlayerName(args[1])).replace("%pl_mobcoins%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && TMMobCoins.PLUGIN.getUtils().getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.remove.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_mobcoins%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                            tp.removeMobcoins(amount); tp.uploadPlayer();
                        } catch (Exception e) {
                            commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.remove.help").prefix().toStringColor());
                    break;
                case "give":
                    if(!commandSender.hasPermission("tmmobcoins.command.give")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    if(args.length > 2) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            MobCoinsPlayer tp = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[1]));
                            commandSender.sendMessage(MessageHandler.message("commands.give.success").prefix()
                                    .replace("%pl_player%", TMMobCoins.PLUGIN.getUtils().getPlayerName(args[1])).replace("%pl_mobcoins%", amount + "")
                                    .placeholderAPI(commandSender).toStringColor());
                            if(!args[1].equalsIgnoreCase(commandSender.getName()) && TMMobCoins.PLUGIN.getUtils().getPlayer(args[1]) instanceof Player)
                                Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.give.received").prefix()
                                        .replace("%pl_player%", commandSender.getName()).replace("%pl_mobcoins%", amount + "")
                                        .placeholderAPI(commandSender).toStringColor());
                            tp.addMobcoins(amount); tp.uploadPlayer();
                        } catch (Exception e) {
                            commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                        }
                    } else commandSender.sendMessage(MessageHandler.message("commands.give.help").prefix().toStringColor());
                    break;
                case "multiplier":
                    /*                 0        1      2       3 */
                    /* /tmmobcoins multiplier give [player] [value] */
                    /* /tmmobcoins multiplier reset [player] */
                    if(!commandSender.hasPermission("tmmobcoins.command.multiplier")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "set":
                            try {
                                double multiplier = Double.parseDouble(args[3]);
                                MobCoinsPlayer tp = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[2]));
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.player").prefix()
                                        .replace("%pl_player%", args[2]).replace("%pl_multiplier%", multiplier + "")
                                        .placeholderAPI(commandSender).toStringColor());
                                tp.setMultiplier(multiplier); tp.uploadPlayer();
                                if(!args[2].equalsIgnoreCase(commandSender.getName()) && TMMobCoins.PLUGIN.getUtils().getPlayer(args[2]) instanceof Player)
                                    Bukkit.getPlayer(args[2]).sendMessage(MessageHandler.message("commands.multiplier.set.otherplayer").prefix()
                                            .replace("%pl_player%", args[2]).replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                            }catch (Exception e) {
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.help").prefix().toStringColor());
                            }
                            break;
                        case "reset":
                            try {
                                double multiplier = 1.0;
                                MobCoinsPlayer tp = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[2]));
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.player").prefix()
                                        .replace("%pl_player%", "All Server").replace("%pl_multiplier%", multiplier + "")
                                        .placeholderAPI(commandSender).toStringColor());
                                tp.setMultiplier(multiplier); tp.uploadPlayer();
                                if(!args[2].equalsIgnoreCase(commandSender.getName()) && TMMobCoins.PLUGIN.getUtils().getPlayer(args[2]) instanceof Player)
                                    Bukkit.getPlayer(args[2]).sendMessage(MessageHandler.message("commands.multiplier.set.otherplayer").prefix()
                                            .replace("%pl_player%", "All Server").replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                            }catch (Exception e) {
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.set.help").prefix().toStringColor());
                            }
                            break;
                        case "global":
                            try {
                                double multiplier = Double.parseDouble(args[2]);
                                if(multiplier != 0) {
                                    FilesManager.FILES.getData().getConfig().set("global_multiplier", multiplier);
                                    FilesManager.FILES.getData().saveConfig();
                                    commandSender.sendMessage(MessageHandler.message("commands.multiplier.global.success").prefix()
                                            .replace("%pl_player%", "All Server").replace("%pl_multiplier%", multiplier + "")
                                            .placeholderAPI(commandSender).toStringColor());
                                } else {
                                    commandSender.sendMessage(MessageHandler.message("commands.multiplier.errors.cant_be").prefix().placeholderAPI(commandSender).toStringColor());
                                }
                            }catch (Exception e) {
                                commandSender.sendMessage(MessageHandler.message("commands.multiplier.global.help").prefix().toStringColor());
                            }
                            break;
                    }
                    break;
                case "balance":
                    if(!commandSender.hasPermission("tmmobcoins.command.balance")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    try {
                        if(args.length == 1) {
                            commandSender.sendMessage(MessageHandler.message("commands.balance.player").prefix()
                                    .replace("%pl_player%", commandSender.getName()).replace("%pl_mobcoins%", "" + StorageAccess.getAccount(((Player) commandSender).getUniqueId()).getMobcoins())
                                    .placeholderAPI(commandSender).toStringColor());
                        } else {
                            commandSender.sendMessage(MessageHandler.message("commands.balance.otherplayer").prefix()
                                    .replace("%pl_player%", args[1]).replace("%pl_mobcoins%", "" + StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[1])).getMobcoins()).placeholderAPI(commandSender).toStringColor());
                        }
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageHandler.message("commands.balance.help").prefix().toStringColor());
                    }
                    break;
                case "pay":
                    if(!commandSender.hasPermission("tmmobcoins.command.pay")) { commandSender.sendMessage(MessageHandler.message("basic.no_permission").prefix().toStringColor()); return true;}
                    try {
                        if(args.length == 3) {
                            try {
                                double amount = Double.parseDouble(args[2]);
                                if(commandSender.getName().equalsIgnoreCase(args[1])) {
                                    commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix()
                                            .replace("%pl_player%", args[1]).replace("%pl_mobcoins%", args[2]).placeholderAPI(commandSender).toStringColor());
                                    break;
                                }
                                MobCoinsPlayer tp = StorageAccess.getAccount(((Player) commandSender).getUniqueId());
                                MobCoinsPlayer tpOtherPlayer = StorageAccess.getAccount(TMMobCoins.PLUGIN.getUtils().getPlayerUUID(args[1]));
                                if(tp.getMobcoins() >= amount) {
                                    tp.removeMobcoins(amount); tp.uploadPlayer();
                                    tpOtherPlayer.addMobcoins(amount); tpOtherPlayer.uploadPlayer();
                                    commandSender.sendMessage(MessageHandler.message("commands.pay.success").prefix()
                                            .replace("%pl_player%", args[1]).replace("%pl_mobcoins%", "" + amount).placeholderAPI(commandSender).toStringColor());
                                    if(TMMobCoins.PLUGIN.getUtils().getPlayer(args[1]) instanceof Player)
                                        Bukkit.getPlayer(args[1]).sendMessage(MessageHandler.message("commands.pay.received").prefix()
                                            .replace("%pl_player%", commandSender.getName()).replace("%pl_mobcoins%", "" + amount).placeholderAPI(commandSender).toStringColor());
                                } else {
                                    commandSender.sendMessage(MessageHandler.message("commands.pay.fail.no_money").prefix()
                                            .replace("%pl_player%", args[1]).replace("%pl_mobcoins%", "" + amount).placeholderAPI(commandSender).toStringColor());
                                }
                            } catch (Exception e) {
                                commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix()
                                        .replace("%pl_player%", args[1]).replace("%pl_mobcoins%", args[2]).placeholderAPI(commandSender).toStringColor());
                            }
                        } else {
                            commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix().placeholderAPI(commandSender).toStringColor());
                        }
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageHandler.message("commands.pay.help").prefix().toStringColor());
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
        if(!commandSender.hasPermission("tmmobcoins.command.help")) {
            commandSender.sendMessage(MessageHandler.chat("\n  &7(v" + TMMobCoins.PLUGIN.getPlugin().getDescription().getVersion() + ")").toStringColor());
            commandSender.sendMessage(MessageHandler.message("basic.no_permission").replace("%pl_prefix%", "").toStringColor());
            commandSender.sendMessage("\n");
        } else {
            commandSender.sendMessage(MessageHandler
                    .chat("\n  <GRADIENT:#FFC837-#FFC837>TMMobcoins</GRADIENT> &7(v" + TMMobCoins.PLUGIN.getPlugin().getDescription().getVersion() + ")\n \n  &f&nArguments&f: &7[] Required, () Optional." +
                            "\n \n  &#f7971e▸ &7/mobcoins give [player] [amount]\n  &#f9a815▸ &7/mobcoins set [player] [amount]" +
                            "\n  &#fab011▸ &7/mobcoins remove [player] [amount]\n  &#fcb90d▸ &7/mobcoins balance (player)\n  &#fdc109▸ &7/mobcoins reload <files/database/all> " +
                            "\n \n  &#17F7C1▸ &7/mobcoins check &#17f7c1[L&#17f6c4e&#16f5c6a&#16f4c9r&#16f4cbn &#16f3cem&#15f2d1o&#15f1d3r&#15f0d6e &#14efd8a&#14eedbb&#14edddo&#14ede0u&#13ece3t &#13ebe5t&#13eae8h&#12e9eai&#12e8eds &#12e7f0p&#11e6f2l&#11e6f5u&#11e5f7g&#11e4fai&#10e3fcn&#10e2ff]\n")
                    .toStringColor());

            if (VersionCheckers.getVersion() >= 16) {
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ Support: \",\"color\":\"#F72B2B\"},{\"text\":\"[Wiki]\",\"color\":\"#F72B2B\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://mobcoins.devtm.net/\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}},{\"text\":\" \",\"color\":\"#F72B2B\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}},{\"text\":\"[Discord]\",\"color\":\"#F72B2B\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.com/invite/XFtV7qgajR\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click To Open\"}}]"));
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ \",\"color\":\"red\"},{\"text\":\" Plugin: \",\"color\":\"red\"},{\"text\":\"[SpigotMC]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/authors/xmaikiyt.508656/\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click to open\"}}]"));
            } else{
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸ Support: \",\"color\":\"red\"},{\"text\":\"[Wiki]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://mobcoins.devtm.net/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}},{\"text\":\" \",\"color\":\"red\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}},{\"text\":\"[Discord]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.com/invite/XFtV7qgajR\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}}]"));
                ((Player) commandSender).spigot().sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"  ▸\",\"color\":\"red\"},{\"text\":\" Plugin: \",\"color\":\"red\"},{\"text\":\"[SpigotMC]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/authors/xmaikiyt.508656/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open\"}}]"));
            }
            commandSender.sendMessage(MessageHandler.chat("\n\n&7&oNote: This plugin is still in the beta stage, if any bugs please report them on our discord server or direct message me on discord (MaikyDev#5343) or make a issues on github! You can find those links on our website!").toStringColor());
        }
    }
}