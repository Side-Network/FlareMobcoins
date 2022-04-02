package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.tmmobcoins.lib.Lib;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class StorageAccess {

    @Nullable
    public static MobCoinsPlayer getAccount(UUID uuid) {
        switch (Objects.requireNonNull(FilesManager.FILES.getConfig().getConfig().getString("storage_type.type")).toLowerCase(Locale.ROOT)) {
            case "file":
                if (!hasAccount(uuid))
                    createAccount(uuid);
                return new MobCoinsPlayer(uuid, FilesManager.FILES.getData().getConfig().getDouble("account." + uuid + ".mobcoins"),
                        FilesManager.FILES.getData().getConfig().getDouble("account." + uuid + ".multiplier"));
            case "database":
                if (!hasAccount(uuid))
                    createAccount(uuid);
                return new MobCoinsPlayer(uuid, Double.parseDouble(Lib.LIB.getMySQL().sqlIO.get("mobcoins", new String[]{"uuid = '" + uuid + "'"}, TMMobCoins.PLUGIN.getUtils().table).toString()),
                        Double.parseDouble(Lib.LIB.getMySQL().sqlIO.get("multiplier", new String[]{"uuid = '" + uuid + "'"}, TMMobCoins.PLUGIN.getUtils().table).toString()));
            case "sqlite":
                TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "NOT AVAILABLE ON THIS VERSION please check our discord for news discord.devtm.net");
                break;
        }
        return null;
    }

    public static boolean hasAccount(UUID uuid) {
        switch (Objects.requireNonNull(FilesManager.FILES.getConfig().getConfig().getString("storage_type.type")).toLowerCase(Locale.ROOT)) {
            case "file":
                return FilesManager.FILES.getData().getConfig().contains("account." + uuid);
            case "database":
                return Lib.LIB.getMySQL().sqlIO.exists("uuid", uuid.toString(), TMMobCoins.PLUGIN.getUtils().table);
            case "sqlite":
                TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "NOT AVAILABLE ON THIS VERSION please check our discord for news discord.devtm.net");
                break;
        }
        return false;
    }

    public static void createAccount(UUID uuid) {
        switch (Objects.requireNonNull(FilesManager.FILES.getConfig().getConfig().getString("storage_type.type")).toLowerCase(Locale.ROOT)) {
            case "file":
                if (!hasAccount(uuid)) {
                    FilesManager.FILES.getData().getConfig().set("account." + uuid + ".mobcoins", TMMobCoins.PLUGIN.getUtils().firstJoinGiveMobcoins);
                    FilesManager.FILES.getData().getConfig().set("account." + uuid + ".multiplier", 1);
                    FilesManager.FILES.getData().saveConfig();
                }
                break;
            case "database":
                if (!hasAccount(uuid)) {
                    Lib.LIB.getMySQL().sqlIO.insertData("player, uuid, mobcoins, multiplier",
                            "'" + TMMobCoins.PLUGIN.getUtils().getPlayerName(uuid) + "', '" + uuid + "', "
                                    + TMMobCoins.PLUGIN.getUtils().firstJoinGiveMobcoins + ", " + 1, TMMobCoins.PLUGIN.getUtils().table);
                }
                break;
            case "sqlite":
                TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "NOT AVAILABLE ON THIS VERSION please check our discord for news discord.devtm.net");
                break;
        }
    }

    public static void insertAccount(MobCoinsPlayer tp) {
        switch (Objects.requireNonNull(FilesManager.FILES.getConfig().getConfig().getString("storage_type.type")).toLowerCase(Locale.ROOT)) {
            case "file":
                if (hasAccount(tp.getUUID()))
                    createAccount(tp.getUUID());
                FilesManager.FILES.getData().getConfig().set("account." + tp.getUUID() + ".mobcoins", tp.getMobcoins());
                FilesManager.FILES.getData().getConfig().set("account." + tp.getUUID() + ".multiplier", tp.getMultiplier());
                FilesManager.FILES.getData().saveConfig();
                break;
            case "database":
                if (hasAccount(tp.getUUID()))
                    createAccount(tp.getUUID());
                Lib.LIB.getMySQL().sqlIO.set("mobcoins", tp.getMobcoins(), "uuid", "=", tp.getUUID().toString(), TMMobCoins.PLUGIN.getUtils().table);
                Lib.LIB.getMySQL().sqlIO.set("multiplier", tp.getMultiplier(), "uuid", "=", tp.getUUID().toString(), TMMobCoins.PLUGIN.getUtils().table);
                break;
            case "sqlite":
                TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "NOT AVAILABLE ON THIS VERSION please check our discord for news discord.devtm.net");
                break;
        }
        TMMobCoins.PLUGIN.getUtils().mobcoinsHistory.remove(tp.getUUID());
        TMMobCoins.PLUGIN.getUtils().mobcoinsTime.remove(tp.getUUID());
    }
}
