package net.devtm.tmmobcoins.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.ServiceHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    @NotNull
    public String getIdentifier() {
        return "tmmobcoins";
    }

    @NotNull
    public String getAuthor() {
        return "TMDevelopment";
    }

    @NotNull
    public String getVersion() {
        return "1.0";
    }

    public boolean canRegister() {
        return true;
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null)
            return "";
        if (params.matches("rotating_shop_(\\w+)_(normal|premium)")) {
            Matcher matcher = Pattern.compile("rotating_shop_(\\w+)_(normal|premium)").matcher(params);
            matcher.find();
            ServiceHandler.SERVICE.getMenuService().updateRotatingShop(matcher.group(1));
            if (!FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time"))
                return params.replace(matcher.group(), "");
            return params.replace(matcher.group(), Utils.UTILS
                    .findDifference(FilesManager.ACCESS.getData().getConfig().getLong("rotating_shop." + matcher.group(1) + "." + matcher.group(2) + "_last_time"), System.currentTimeMillis()));
        }
        if (params.contains("get_mobcoins"))
            return String.valueOf(ServiceHandler.SERVICE.getDataService().wrapPlayer(p.getUniqueId()).getMobcoins());
        if (params.contains("get_commas_mobcoins"))
            return String.format("%,.2f", ServiceHandler.SERVICE.getDataService().wrapPlayer(p.getUniqueId()).getMobcoins());
        if (params.contains("get_formatted_mobcoins")) {
            double mobcoins = ServiceHandler.SERVICE.getDataService().wrapPlayer(p.getUniqueId()).getMobcoins();
            DecimalFormat format = new DecimalFormat("0.#");
            if (mobcoins < 1000.0D)
                return format.format(mobcoins);
            int exp = (int)(Math.log(mobcoins) / Math.log(1000.0D));
            String value = format.format(mobcoins / Math.pow(1000.0D, exp));
            return String.format("%s%c", value, "kMBT".charAt(exp - 1));
        }
        if (params.contains("get_multiplier"))
            return String.valueOf(ServiceHandler.SERVICE.getDataService().wrapPlayer(p.getUniqueId()).getMultiplier());
        if (params.contains("get_server_multiplier"))
            return String.valueOf(FilesManager.ACCESS.getData().getConfig().getDouble("global_multiplier"));
        return null;
    }
}
