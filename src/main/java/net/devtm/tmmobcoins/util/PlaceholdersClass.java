package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.tmmobcoins.lib.base.CustomPlaceholders;
import org.bukkit.entity.Player;

public class PlaceholdersClass implements CustomPlaceholders {

    @Override
    public String process(String text, Player player) {
        text = text.replace("%pl_mobcoins%", TMMobCoins.PLUGIN.getUtils().getMobCoinsInDelay(player.getUniqueId()) + "");
        text = text.replace("%pl_time_left_normal%", TMMobCoins.PLUGIN.getUtils().findDifference(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.normal"), System.currentTimeMillis()));
        text = text.replace("%pl_time_left_special%", TMMobCoins.PLUGIN.getUtils().findDifference(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.special"), System.currentTimeMillis()));
        return text;
    }
}
