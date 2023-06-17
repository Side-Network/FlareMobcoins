package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

public enum Utils {
    UTILS;

    private final NumberFormat doubleFormat;

    Utils() {
        this.doubleFormat = new DecimalFormat("#0.00");
    }

    public NumberFormat getDoubleFormat() {
        return this.doubleFormat;
    }

    public void reloadUtils() {}

    public Object getPlayer(String s) {
        return (Bukkit.getPlayer(s) != null) ? Bukkit.getPlayer(s) : Bukkit.getOfflinePlayer(s);
    }

    public UUID getPlayerUUID(String s) {
        return (Bukkit.getPlayer(s) != null) ? Bukkit.getPlayer(s).getUniqueId() : Bukkit.getOfflinePlayer(s).getUniqueId();
    }

    public String getPlayerName(String s) {
        return (Bukkit.getPlayer(s) != null) ? Bukkit.getPlayer(s).getName() : Bukkit.getOfflinePlayer(s).getName();
    }

    public String getPlayerName(UUID s) {
        return (Bukkit.getPlayer(s) != null) ? Bukkit.getPlayer(s).getName() : Bukkit.getOfflinePlayer(s).getName();
    }

    public String findDifference(long start_date, long end_date) {
        long difference_In_Time = start_date - end_date;
        long difference_In_Seconds = difference_In_Time / 1000L % 60L;
        long difference_In_Minutes = difference_In_Time / 60000L % 60L;
        long difference_In_Hours = difference_In_Time / 3600000L % 24L;
        long difference_In_Years = difference_In_Time / 31536000000L;
        long difference_In_Days = difference_In_Time / 86400000L % 365L;
        //noinspection ConstantConditions
        return FilesManager.ACCESS.getConfig().getConfig().getString("rotating_shop_time_format")
                .replace("%years%", difference_In_Years + "")
                .replace("%days%", difference_In_Days + "")
                .replace("%hours%", difference_In_Hours + "")
                .replace("%min%", difference_In_Minutes + "")
                .replace("%sec%", difference_In_Seconds + "");
    }

    public static FileConfiguration readConfig(String file) {
        return YamlConfiguration.loadConfiguration(new File(TMMobCoins.PLUGIN.getPlugin().getDataFolder(), file));
    }

    public static FileConfiguration readConfig(Path file) {
        return YamlConfiguration.loadConfiguration(new File(file.toString()));
    }
}
