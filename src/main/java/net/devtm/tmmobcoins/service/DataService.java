package net.devtm.tmmobcoins.service;

import net.devtm.tmmobcoins.API.MobcoinsPlayer;
import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.util.Utils;
import net.tmmobcoins.lib.Lib;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DataService {
    String storageType = null;

    public String table = null;
    private final Map<UUID, MobcoinsPlayer> playerCache = new HashMap<>();
    private final Map<UUID, MobcoinsPlayer> autoSave = new HashMap<>();

    public int firstMobcoins = 0;

    public DataService() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimer(TMMobCoins.PLUGIN.getPlugin(), 0L,20 * 60 * 5L);
    }

    public void saveAll() {
        for (MobcoinsPlayer player : autoSave.values()) {
            setMobcoins(player.getPlayer(), player.getMobcoins());
        }
        autoSave.clear();
    }

    public MobcoinsPlayer wrapPlayer(UUID uuid) {
        MobcoinsPlayer mobcoinsPlayer = playerCache.get(uuid);
        if (mobcoinsPlayer == null) {
            createPlayerProfile(uuid);

            mobcoinsPlayer = new MobcoinsPlayer();
            mobcoinsPlayer.setPlayer(uuid);
            mobcoinsPlayer.setMobcoins(getMobcoins(uuid));
            mobcoinsPlayer.setMultiplier(getMultiplier(uuid));
            playerCache.put(uuid, mobcoinsPlayer);
        }

        return mobcoinsPlayer;
    }

    public void reloadDataService() {
        try {
            this.storageType = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type").toLowerCase(Locale.ROOT);
            this.table = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table");
            this.firstMobcoins = FilesManager.ACCESS.getConfig().getConfig().getInt("first_join_give_mobcoins");
        } catch (Exception ignored) {}
    }

    public double getMobcoins(UUID uuid) {
        return switch (this.storageType) {
            case "file" -> FilesManager.ACCESS.getData().getConfig().getDouble("account." + uuid + ".mobcoins");
            case "mysql" -> Double.parseDouble(Lib.LIB.getSql().getData("mobcoins", uuid.toString(), this.table).toString());
            default -> 0.0D;
        };
    }

    public double getMultiplier(UUID uuid) {
        return switch (this.storageType) {
            case "file" -> FilesManager.ACCESS.getData().getConfig().getDouble("account." + uuid + ".multiplier");
            case "mysql" -> Double.parseDouble(Lib.LIB.getSql().getData("multiplier", uuid.toString(), this.table).toString());
            default -> 0.0D;
        };
    }

    public void setMobcoins(UUID uuid, double amount) {
        Double mobcoins = Double.parseDouble(Utils.UTILS.getDoubleFormat().format(amount));

        switch (this.storageType) {
            case "file" -> {
                FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".mobcoins", mobcoins);
                FilesManager.ACCESS.getData().saveConfig();
            }
            case "mysql" -> Lib.LIB.getSql().updatePlayerData("mobcoins", String.valueOf(mobcoins), uuid.toString(), this.table);
        }
    }

    public void setMultiplier(UUID uuid, double multiplier) {
        switch (this.storageType) {
            case "file" -> {
                FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".multiplier", multiplier);
                FilesManager.ACCESS.getData().saveConfig();
            }
            case "mysql" -> Lib.LIB.getSql().updatePlayerData("multiplier", String.valueOf(multiplier), uuid.toString(), this.table);
        }
    }

    public void createPlayerProfile(UUID uuid) {
        switch (this.storageType) {
            case "file":
                if (!FilesManager.ACCESS.getData().getConfig().contains("account." + uuid)) {
                    FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".mobcoins", this.firstMobcoins);
                    FilesManager.ACCESS.getData().getConfig().set("account." + uuid + ".multiplier", 1);
                    FilesManager.ACCESS.getData().saveConfig();
                }
                break;
            case "mysql":
                if (!Lib.LIB.getSql().playerDataExists(uuid.toString(), this.table)) {
                    Lib.LIB.getSql().insertData(
                            "player, uuid, mobcoins, multiplier",
                            this.table,
                            Utils.UTILS.getPlayerName(uuid), uuid.toString(), String.valueOf(this.firstMobcoins), "1.00");
                }
                break;
        }
    }

    public void markForAutoSave(MobcoinsPlayer mobcoinsPlayer) {
        if (!autoSave.containsKey(mobcoinsPlayer.getPlayer()))
            autoSave.put(mobcoinsPlayer.getPlayer(), mobcoinsPlayer);
    }
}
