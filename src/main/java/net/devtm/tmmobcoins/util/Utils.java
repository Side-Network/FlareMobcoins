package net.devtm.tmmobcoins.util;

import net.devtm.tmmobcoins.files.FilesManager;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.menu.Menu;
import net.tmmobcoins.lib.menu.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Utils {

  /**
   * IF you are watching this I will change it soon it's a mess
   */

  public int firstJoinGiveMobcoins = FilesManager.ACCESS.getConfig().getConfig().getInt("first_join_give_mobcoins");

  public String table = FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table");

  public HashMap<UUID, Double> mobcoinsHistory = new HashMap<>();
  public HashMap<UUID, Long> mobcoinsTime = new HashMap<>();

  public HashMap<UUID, Double> multiplierHistory = new HashMap<>();
  public HashMap<UUID, Long> multiplierTime = new HashMap<>();

  public void runnable(JavaPlugin plugin) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if(!FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.shop_type").equalsIgnoreCase("rotating")) return;
        if(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.normal") <= System.currentTimeMillis()) {
          regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "normal");
          FilesManager.ACCESS.getData().getConfig().set("refresh_data.normal", System.currentTimeMillis()
                  + (FilesManager.ACCESS.getConfig().getConfig().getInt("shop.settings.rotating_item.settings.normal.refresh_time") * 1000L));
          FilesManager.ACCESS.getData().saveConfig();
        }
        if(FilesManager.ACCESS.getData().getConfig().getLong("refresh_data.special")  <= System.currentTimeMillis()) {
          regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "special");
          FilesManager.ACCESS.getData().getConfig().set("refresh_data.special", System.currentTimeMillis()
                  + (FilesManager.ACCESS.getConfig().getConfig().getInt("shop.settings.rotating_item.settings.special.refresh_time") * 1000L));
          FilesManager.ACCESS.getData().saveConfig();
        }
      }
    }.runTaskTimerAsynchronously(plugin, 1, 1);
  }

  public void showMainShop(Player player) {

    if (!player.hasPermission("mobcoins.shop")) return;

    Configuration config = FilesManager.ACCESS.getConfig().getConfig();
    if(!FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.shop_type").equalsIgnoreCase("rotating")) {
      Menu menu = new Menu(player, MessageHandler.chat(config.getString("shop.menu_title")).placeholderAPI(player).toStringColor(), config.getInt("shop.size"));
      for (String s1 : config.getConfigurationSection("shop.items").getKeys(false))
        menu.assignItems(new ItemHandler().setPlayer(player.getPlayer()).autoGetter(config, "shop.items", s1));
      menu.updateInventory();
      player.openInventory(menu.inventory);
    } else {
      Menu menu = new Menu(player, MessageHandler.chat(config.getString("shop.menu_title")).placeholderAPI(player).toStringColor(), config.getInt("shop.size"));
      for (String s1 : config.getConfigurationSection("shop.items").getKeys(false))
        menu.assignItems(new ItemHandler().setPlayer(player.getPlayer()).autoGetter(config, "shop.items", s1));
      for(String s : FilesManager.ACCESS.getData().getConfig().getStringList("refresh_data.items_in_storage.normal"))
        menu.assignItems(new ItemHandler().setPlayer(player.getPlayer()).autoGetter(config, "shop.items", s.split(";")[0]).setSlots(Integer.parseInt(s.split(";")[1])));
      for(String s : FilesManager.ACCESS.getData().getConfig().getStringList("refresh_data.items_in_storage.special"))
        menu.assignItems(new ItemHandler().setPlayer(player.getPlayer()).autoGetter(config, "shop.items", s.split(";")[0]).setSlots(Integer.parseInt(s.split(";")[1])));
      menu.updateInventory();
      player.openInventory(menu.inventory);
    }
  }

  public void regenerateItems(@NotNull Configuration config, String type) {
    if(!FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.shop_type").equalsIgnoreCase("rotating")) return;
    HashMap<String, Integer> dataNormal = new HashMap<>();
    HashMap<String, Integer> dataSpecial = new HashMap<>();
    List<String> dataNormalSave = new ArrayList<>();
    List<String> dataSpecialSave = new ArrayList<>();
    /* Read data */
    List<Integer> normalSlots = config.getIntegerList("shop.settings.rotating_item.settings.normal.slots");
    List<Integer> specialSlots = config.getIntegerList("shop.settings.rotating_item.settings.special.slots");
    List<String> normalItemList = config.getStringList("shop.settings.rotating_item.settings.normal.item_list");
    List<String> specialItemList = config.getStringList("shop.settings.rotating_item.settings.special.item_list");
    /* process */
    boolean generateProcess = true;
    int cursor = 0;
    Random rand = new Random();
    while (generateProcess) {
      if (type.equalsIgnoreCase("normal")) {
        if (cursor < normalSlots.size()) {
          int gen = rand.nextInt(normalItemList.size());
          if (!dataNormal.containsKey(normalItemList.get(gen))) {
            dataNormal.put(normalItemList.get(gen), normalSlots.get(cursor));
            dataNormalSave.add(normalItemList.get(gen) + ";" + normalSlots.get(cursor++));
          }
        } else {
          generateProcess = false;
        }
      } else {
        if (cursor < specialSlots.size()) {
          int gen = rand.nextInt(specialItemList.size());
          if (!dataSpecial.containsKey(specialItemList.get(gen))) {
            dataSpecial.put(specialItemList.get(gen), specialSlots.get(cursor));
            dataSpecialSave.add(specialItemList.get(gen) + ";" + specialSlots.get(cursor++));
          }
        } else {
          generateProcess = false;
        }
      }
    }
    /* save items */
    if (type.equalsIgnoreCase("normal"))
      FilesManager.ACCESS.getData().getConfig().set("refresh_data.items_in_storage.normal", dataNormalSave);
    else
      FilesManager.ACCESS.getData().getConfig().set("refresh_data.items_in_storage.special", dataSpecialSave);
    FilesManager.ACCESS.getData().saveConfig();
  }

  /**
   * Used to not spam the Database or File with requests
   * It will provide an old mobcoins value like 10 seconds and it will be updated every time someone change sometihng in database
   * @param uuid
   * @return
   */
  public double getMobCoinsInDelay(UUID uuid) {
    int factor = FilesManager.ACCESS.getConfig().getConfig().getInt("storage_type.read_delay");
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

  public double getMultiplierInDelay(UUID uuid) {
    int factor = FilesManager.ACCESS.getConfig().getConfig().getInt("storage_type.read_delay");
    if(multiplierTime.containsKey(uuid)) {
      if(multiplierTime.get(uuid) < System.currentTimeMillis()) {
        multiplierTime.put(uuid, System.currentTimeMillis() + factor);
        multiplierHistory.put(uuid, StorageAccess.getAccount(uuid).getMultiplier());
      }
      return multiplierHistory.get(uuid);
    } else {
      multiplierTime.put(uuid, System.currentTimeMillis() + factor);
      multiplierHistory.put(uuid, StorageAccess.getAccount(uuid).getMultiplier());
    }
    return multiplierHistory.get(uuid);
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
    public String findDifference(long start_date, long end_date) {
      long difference_In_Time = start_date - end_date;
      long difference_In_Seconds = (difference_In_Time / 1000) % 60;
      long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
      long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
      long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
      long difference_In_Days = (difference_In_Time  / (1000 * 60 * 60 * 24))  % 365;
      return difference_In_Days + " days "
              + difference_In_Hours + " hours " + difference_In_Minutes  + " minutes " + difference_In_Seconds + " seconds";
    }
}
