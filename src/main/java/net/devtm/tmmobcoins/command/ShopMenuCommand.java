package net.devtm.tmmobcoins.command;

import net.devtm.tmmobcoins.files.FilesManager;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.menu.Menu;
import net.tmmobcoins.lib.menu.item.ItemHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class ShopMenuCommand extends BukkitCommand {

    String description;

    public ShopMenuCommand(String cmdName, String description) {
        super(cmdName);
        this.description = description;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        if(!commandSender.hasPermission("mobcoins.shop")) return true;
        Configuration config = FilesManager.FILES.getConfig().getConfig();
        Menu menu = new Menu((Player) commandSender, MessageHandler.chat(config.getString("shop.menu_title")).placeholderAPI(commandSender).toStringColor(), config.getInt("shop.size"));
        for (String s1 : config.getConfigurationSection("shop.items").getKeys(false))
            menu.assignItems(new ItemHandler().setPlayer(((Player) commandSender).getPlayer()).autoGetter(config, "shop.items", s1));
        menu.updateInventory();
        ((Player) commandSender).openInventory(menu.inventory);

        return true;
    }
}
