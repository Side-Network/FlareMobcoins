package net.devtm.tmmobcoins.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.util.ShopStock;
import net.devtm.tmmobcoins.util.StockProfile;
import net.devtm.tmmobcoins.util.Utils;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.menu_modified.Menu;
import net.tmmobcoins.lib.menu_modified.item.ItemHandler;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class MenuService {
    public void openMenu(Player player, String s) {
        FileConfiguration config = Utils.readConfig("shop/" + s + ".yml");
        if (config.contains("menu_permission") &&
                !player.hasPermission(config.getString("menu_permission")))
            return;
        if (config.contains("open_requirement")) {
            TMPL tmpl = new TMPL();
            tmpl.setCode(Utils.readConfig("shops/" + s).getStringList("open_requirement"));
            if (!tmpl.process(player))
                return;
        }
        Menu menu = config.contains("menu_type") ? new Menu(player, config.getString("menu_title"), InventoryType.valueOf(config.getString("menu_type")), s) : new Menu(player, config.getString("menu_title"), config.getInt("size"), s);
        for (String s1 : config.getConfigurationSection("items").getKeys(false))
            menu.assignItems((new ItemHandler()).setPlayer(player).setShopName(s).autoGetter((Configuration)config, "items", s1));
        if (config.contains("rotating_shop"))
            menu = createRotatingMenu(player, s, menu);
        menu.updateContent();
        player.openInventory(menu.getInventory());
    }

    public void updateRotatingShop(String shopName) {
        FileConfiguration data = FilesManager.ACCESS.getData().getConfig();
        FileConfiguration shop = Utils.readConfig("shop/" + shopName + ".yml");
        if (!data.contains("rotating_shop." + shopName))
            initData(shopName);
        if (data.getLong("rotating_shop." + shopName + ".normal_last_time") <= System.currentTimeMillis()) {
            generateItems(shopName, 1);
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + shopName + ".normal_last_time", System.currentTimeMillis() + shop.getInt("rotating_shop.normal_refresh") * 1000L);
            FilesManager.ACCESS.getData().saveConfig();
        }
        if (data.getLong("rotating_shop." + shopName + ".premium_last_time") <= System.currentTimeMillis()) {
            generateItems(shopName, 2);
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + shopName + ".premium_last_time", System.currentTimeMillis() + shop.getInt("rotating_shop.premium_refresh") * 1000L);
            FilesManager.ACCESS.getData().saveConfig();
        }
    }

    private Menu createRotatingMenu(Player player, String shopName, Menu menu) {
        updateRotatingShop(shopName);
        FileConfiguration shop = Utils.readConfig("shop/" + shopName + ".yml");
        for (String key : FilesManager.ACCESS.getData().getConfig().getConfigurationSection("rotating_shop." + shopName + ".items_normal").getKeys(false))
            menu.assignItems((new ItemHandler()).setPlayer(player).setShopName(shopName).autoGetter((Configuration)shop, "items", key).setSlots(FilesManager.ACCESS.getData().getConfig().getInt("rotating_shop." + shopName + ".items_normal." + key + ".slot")));
        for (String key : FilesManager.ACCESS.getData().getConfig().getConfigurationSection("rotating_shop." + shopName + ".items_premium").getKeys(false))
            menu.assignItems((new ItemHandler()).setPlayer(player).setShopName(shopName).autoGetter((Configuration)shop, "items", key).setSlots(FilesManager.ACCESS.getData().getConfig().getInt("rotating_shop." + shopName + ".items_premium." + key + ".slot")));
        return menu;
    }

    public StockProfile getPlayerStock(Player player, String shopName, String itemName) {
        int stockLeft, stockLeftPlayer;
        FileConfiguration data = FilesManager.ACCESS.getData().getConfig();
        if (!FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + shopName + ".items_normal." + itemName) && !FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + shopName + ".items_premium." + itemName))
            return null;
        String stockType = FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + shopName + ".items_normal." + itemName) ? "items_normal" : "items_premium";
        switch (data.getString("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock_type")) {
            case "SERVER" -> {
                stockLeft = data.contains("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data.server_stock") ? data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data.server_stock") : data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock");
                return new StockProfile(stockLeft, ShopStock.StockType.valueOf(data.getString("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock_type")), null, data
                        .getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock"));
            }
            case "PLAYER" -> {
                stockLeftPlayer = data.contains("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data." + player.getName()) ? data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data." + player.getName()) : data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock");
                return new StockProfile(stockLeftPlayer, ShopStock.StockType.valueOf(data.getString("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock_type")), player, data
                        .getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock"));
            }
        }
        return null;
    }

    public boolean canBuyItem(Player player, String shopName, String itemName) {
        StockProfile stockProfile = getPlayerStock(player, shopName, itemName);
        if (stockProfile == null)
            return true;
        return stockProfile.getStock() > 0;
    }

    public void decreaseStock(Player player, String shopName, String itemName) {
        FileConfiguration data = FilesManager.ACCESS.getData().getConfig();
        StockProfile stockProfile = getPlayerStock(player, shopName, itemName);
        int stockLeft, stockLeftPlayer;
        
        String stockType = FilesManager.ACCESS.getData().getConfig().contains("rotating_shop." + shopName + ".items_normal." + itemName) ? "items_normal" : "items_premium";
        switch (stockProfile.getType().toString()) {
            case "SERVER" -> {
                stockLeft = data.contains("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data.server_stock") ? data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data.server_stock") : data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock");
                data.set("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data.server_stock", --stockLeft);
            }
            case "PLAYER" -> {
                stockLeftPlayer = data.contains("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data." + player.getName()) ? data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data." + player.getName()) : data.getInt("rotating_shop." + shopName + "." + stockType + "." + itemName + ".stock");
                data.set("rotating_shop." + shopName + "." + stockType + "." + itemName + ".data." + player.getName(), --stockLeftPlayer);
            }
        }
        FilesManager.ACCESS.getData().saveConfig();
    }

    private void generateItems(String s, int which) {
        FileConfiguration shop = Utils.readConfig("shop/" + s + ".yml");
        if (which == 1 || which == 3)
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_normal", null);
        if (which == 2 || which == 3)
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_premium", null);
        List<Integer> normalSlots = shop.getIntegerList("rotating_shop.normal_slots");
        List<Integer> premiumSlots = shop.getIntegerList("rotating_shop.premium_slots");
        List<String> normalItems = new ArrayList<>(shop.getConfigurationSection("rotating_shop.normal_items").getKeys(false));
        List<String> premiumItems = new ArrayList<>(shop.getConfigurationSection("rotating_shop.premium_items").getKeys(false));
        while (normalSlots.size() != 0 && (which == 1 || which == 3)) {
            int itemId = (new Random()).nextInt(normalItems.size());
            int slotId = (new Random()).nextInt(normalSlots.size());
            ShopStock stock = new ShopStock();
            Matcher matcher = Pattern.compile("stock\\((server|player)\\,(\\d+)\\)").matcher(shop.getString("rotating_shop.normal_items." + (String)normalItems.get(itemId)));
            matcher.find();
            stock.setType(ShopStock.StockType.valueOf(matcher.group(1).toUpperCase(Locale.ROOT)));
            stock.setStock(Integer.parseInt(matcher.group(2)));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_normal." + (String)normalItems.get(itemId) + ".slot", normalSlots.get(slotId));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_normal." + (String)normalItems.get(itemId) + ".stock", Integer.valueOf(stock.getStock()));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_normal." + (String)normalItems.get(itemId) + ".stock_type", stock.getType().toString());
            normalItems.remove(itemId);
            normalSlots.remove(slotId);
        }
        while (premiumSlots.size() != 0 && (which == 2 || which == 3)) {
            int itemId = (new Random()).nextInt(premiumItems.size());
            int slotId = (new Random()).nextInt(premiumSlots.size());
            ShopStock stock = new ShopStock();
            Matcher matcher = Pattern.compile("stock\\((server|player)\\,(\\d+)\\)").matcher(shop.getString("rotating_shop.premium_items." + (String)premiumItems.get(itemId)));
            matcher.find();
            stock.setType(ShopStock.StockType.valueOf(matcher.group(1).toUpperCase(Locale.ROOT)));
            stock.setStock(Integer.parseInt(matcher.group(2)));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_premium." + (String)premiumItems.get(itemId) + ".slot", premiumSlots.get(slotId));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_premium." + (String)premiumItems.get(itemId) + ".stock", Integer.valueOf(stock.getStock()));
            FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".items_premium." + (String)premiumItems.get(itemId) + ".stock_type", stock.getType().toString());
            premiumItems.remove(itemId);
            premiumSlots.remove(slotId);
        }
        FilesManager.ACCESS.getData().saveConfig();
    }

    private void initData(String s) {
        FileConfiguration shop = Utils.readConfig("shop/" + s + ".yml");
        FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".normal_last_time", Long.valueOf(System.currentTimeMillis() + shop.getInt("rotating_shop.normal_refresh") * 1000L));
        FilesManager.ACCESS.getData().getConfig().set("rotating_shop." + s + ".premium_last_time", Long.valueOf(System.currentTimeMillis() + shop.getInt("rotating_shop.premium_refresh") * 1000L));
        FilesManager.ACCESS.getData().saveConfig();
        generateItems(s, 3);
    }
}
