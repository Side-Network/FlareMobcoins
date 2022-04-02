package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.files.FilesManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class Utils {

  /**
   * IF you are watching this I will change it soon it's a mess
   */

  public int firstJoinGiveMobcoins = FilesManager.FILES.getConfig().getConfig().getInt("first_join_give_mobcoins");

  public String table = FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.table");

  public HashMap<UUID, Double> mobcoinsHistory = new HashMap<>();
  public HashMap<UUID, Long> mobcoinsTime = new HashMap<>();



  /**
   * Used to not spam the Database or File with requests
   * It will provide an old mobcoins value like 10 seconds and it will be updated every time someone change sometihng in database
   * @param uuid
   * @return
   */
  public double getMobCoinsInDelay(UUID uuid) {
    int factor = FilesManager.FILES.getConfig().getConfig().getInt("storage_type.read_delay");
    if(mobcoinsTime.containsKey(uuid)) {
      if(mobcoinsTime.get(uuid) < System.currentTimeMillis()) {
        mobcoinsTime.put(uuid, System.currentTimeMillis() + factor);
        mobcoinsHistory.put(uuid, StorageAccess.getAccount(uuid).getMobcoins());
      }
      return mobcoinsHistory.get(uuid);
    } else {
      mobcoinsTime.put(uuid, System.currentTimeMillis() + factor);
      mobcoinsHistory.put(uuid, StorageAccess.getAccount(uuid).getMobcoins());
    }
    return mobcoinsHistory.get(uuid);
  }

  public Object getPlayer(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s); else return Bukkit.getOfflinePlayer(s);
  }
  public UUID getPlayerUUID(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getUniqueId(); else return Bukkit.getOfflinePlayer(s).getUniqueId();
  }
  public String getPlayerName(String s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getName(); else return Bukkit.getOfflinePlayer(s).getName();
  }
  public String getPlayerName(UUID s) {
    if(Bukkit.getPlayer(s) != null) return Bukkit.getPlayer(s).getName(); else return Bukkit.getOfflinePlayer(s).getName();
  }

}
