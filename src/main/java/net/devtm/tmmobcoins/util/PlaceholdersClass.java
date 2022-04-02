package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.TMMobCoins;
import net.tmmobcoins.lib.base.CustomPlaceholders;
import org.bukkit.entity.Player;

public class PlaceholdersClass implements CustomPlaceholders {

    @Override
    public String process(String text, Player player) {
        text = text.replace("%pl_mobcoins%", TMMobCoins.PLUGIN.getUtils().getMobCoinsInDelay(player.getUniqueId()) + "");
        return text;
    }
}
