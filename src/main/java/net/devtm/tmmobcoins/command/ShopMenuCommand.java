package net.devtm.tmmobcoins.command;

import net.devtm.tmmobcoins.TMMobCoins;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopMenuCommand extends BukkitCommand {

    String description;

    public ShopMenuCommand(String cmdName, String description) {
        super(cmdName);
        this.description = description;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String label, String[] args) {
        TMMobCoins.PLUGIN.getUtils().showMainShop((Player) commandSender);
        return true;
    }
}
