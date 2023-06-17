package net.devtm.tmmobcoins.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.tmmobcoins.lib.base.CustomPlaceholders;
import org.bukkit.entity.Player;

public class PlaceholdersClass implements CustomPlaceholders {
    public String process(String text, Player player) {
        text = text.replace("%pl_mobcoins%", ServiceHandler.SERVICE.getDataService().wrapPlayer(player.getUniqueId()).getFormattedMobcoins());
        if (text.contains("%pl_rotating_shop")) {
            Matcher matcher = Pattern.compile("%pl_rotating_shop_(\\w+)_(normal|premium)%").matcher(text);
            matcher.find();
            ServiceHandler.SERVICE.getMenuService().updateRotatingShop(matcher.group(1));
            if (!FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time")) {
                text = text.replace(matcher.group(), "");
                return text;
            }
            text = text.replace(matcher.group(), Utils.UTILS
                    .findDifference(FilesManager.ACCESS.getData().getConfig().getLong("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time"), System.currentTimeMillis()));
        }
        return text;
    }
}
