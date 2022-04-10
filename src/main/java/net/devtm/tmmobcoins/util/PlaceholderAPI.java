package net.devtm.tmmobcoins.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.devtm.tmmobcoins.TMMobCoins;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "tmmobcoins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TMDevelopment";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player p, String params) {
        if (p == null)
            return "";
        if (params.contains("get_mobcoins"))
            return String.valueOf(TMMobCoins.PLUGIN.getUtils().getMobCoinsInDelay(p.getUniqueId()));
        else if (params.contains("multiplier"))
            return String.valueOf(TMMobCoins.PLUGIN.getUtils().getMultiplierInDelay(p.getUniqueId()));
        return null;
    }

}
