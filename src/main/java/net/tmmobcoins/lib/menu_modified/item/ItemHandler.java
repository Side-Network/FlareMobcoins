package net.tmmobcoins.lib.menu_modified.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.devtm.tmmobcoins.util.StockProfile;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.base.VersionCheckers;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHandler {
    public String shopName;

    public String itemName;

    public ItemStack item;

    public String displayName;

    public String viewRequirement;

    public String material;

    public int customData;

    public List<String> lore;

    public List<String> onClickCommands;

    public List<Integer> slot = new ArrayList<>();

    public List<String> viewRequirementList = new ArrayList<>();

    public boolean view = true;

    private Player player;

    public boolean update;

    public boolean glow;

    public int priority = 99;

    public int amount = 1;

    public static ItemHandler item() {
        return new ItemHandler();
    }

    public ItemHandler autoGetter(Configuration config, String pre, String itemName) {
        this.itemName = itemName;
        setMaterial(config.getString(pre + "." + itemName + ".material"));
        setDisplay(config.getString(pre + "." + itemName + ".display_name"), config.getStringList(pre + "." + itemName + ".lore"));
        setInformation(config.getInt(pre + "." + itemName + ".data"), config.getBoolean(pre + "." + itemName + ".update"));
        if (config.contains(pre + "." + itemName + ".click_commands")) {
            setClickCommands(config.getStringList(pre + "." + itemName + ".click_commands"));
        } else {
            setClickCommands(null);
        }
        if (config.contains(pre + "." + itemName + ".view_requirement")) {
            this.viewRequirement = config.getString(pre + "." + itemName + ".view_requirement");
        } else {
            this.viewRequirement = null;
        }
        if (config.contains(pre + "." + itemName + ".view_requirement_list")) {
            this.viewRequirementList = config.getStringList(pre + "." + itemName + ".view_requirement_list");
        } else {
            this.viewRequirementList = null;
        }
        if (this.viewRequirement != null)
            this.view = getViewRequirement(this.viewRequirement);
        if (this.viewRequirementList != null)
            this.view = getViewRequirementList(this.viewRequirementList);
        if (config.contains(pre + "." + itemName + ".priority"))
            this.priority = config.getInt(pre + "." + itemName + ".priority");
        if (config.contains(pre + "." + itemName + ".glow"))
            this.glow = config.getBoolean(pre + "." + itemName + ".glow");
        if (config.contains(pre + "." + itemName + ".amount"))
            this.amount = config.getInt(pre + "." + itemName + ".amount");
        if (config.contains(pre + "." + itemName + ".slots")) {
            setSlots(config.getIntegerList(pre + "." + itemName + ".slots"));
        } else if (config.contains(pre + "." + itemName + ".slot")) {
            setSlots(config.getInt(pre + "." + itemName + ".slot"));
        } else if (config.contains(pre + "." + itemName + ".slot")) {
            setSlots(config.getInt(pre + "." + itemName + ".slot"));
        }
        return this;
    }

    public ItemHandler setShopName(String shopName) {
        this.shopName = shopName;
        return this;
    }

    public ItemHandler setViewRequirement(String viewRequirement) {
        this.viewRequirement = viewRequirement;
        return this;
    }

    public ItemHandler setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemHandler setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public ItemHandler setSlots(List<Integer> slot) {
        this.slot = slot;
        return this;
    }

    public ItemHandler setSlots(int slot) {
        this.slot.clear();
        this.slot.add(Integer.valueOf(slot));
        return this;
    }

    public ItemHandler setInformation(int customData, boolean update) {
        this.update = update;
        this.customData = customData;
        return this;
    }

    public ItemHandler setMaterial(String material) {
        this.material = material;
        return this;
    }

    public ItemHandler setDisplay(String displayName, List<String> lore) {
        this.lore = lore;
        this.displayName = displayName;
        return this;
    }

    public ItemHandler setClickCommands(List<String> onClickCommands) {
        this.onClickCommands = onClickCommands;
        return this;
    }

    public boolean getViewRequirement(String s) {
        CodeArray ca = new CodeArray();
        ca.setConditions(Collections.singletonList(s));
        return ca.checkRequirement(this.player);
    }

    public boolean getViewRequirementList(List<String> s) {
        CodeArray ca = new CodeArray();
        ca.setConditions(s);
        return ca.checkRequirement(this.player);
    }

    public void onItemClick(Player player, ClickType clickType, String shopName, String itemName) {
        if (this.onClickCommands != null) {
            TMPL tmpl = new TMPL();
            tmpl.setCode(this.onClickCommands);
            boolean result = tmpl.process(player);
            tmpl.provideClickType(clickType);

            if (result)
                ServiceHandler.SERVICE.getMenuService().decreaseStock(player, shopName, itemName);
        }
    }

    private String customPlaceholders(String s) {
        StockProfile stockProfile = ServiceHandler.SERVICE.getMenuService().getPlayerStock(this.player, this.shopName, this.itemName);
        if (stockProfile != null)
            s = s.replace("%pl_stock%", stockProfile.getStock() + "").replace("%pl_max_stock%", stockProfile.getMaxStock() + "");
        return s;
    }

    private List<String> loreProcess(List<String> list) {
        List<String> stringList = new ArrayList<>();
        StockProfile stockProfile = ServiceHandler.SERVICE.getMenuService().getPlayerStock(this.player, this.shopName, this.itemName);
        if (stockProfile != null) {
            list.forEach(s -> stringList.add(MessageHandler.chat(s).placeholderAPI(this.player).replace("%pl_stock%", stockProfile.getStock() + "").replace("%pl_max_stock%", stockProfile.getMaxStock() + "").toStringColor()));
        } else {
            list.forEach(s -> stringList.add(MessageHandler.chat(s).placeholderAPI(this.player).toStringColor()));
        }
        return stringList;
    }

    public ItemStack build() {
        if (this.item == null)
            if (this.material.contains("[HEAD]")) {
                this.item = SkullHandler.getHeadFromValue(this.material.replace("[HEAD] ", ""));
            } else {
                this.item = new ItemStack(Material.getMaterial(this.material.toUpperCase()));
            }
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(customPlaceholders(MessageHandler.chat(this.displayName).placeholderAPI(this.player).toStringColor()));
        meta.setLore(loreProcess(this.lore));
        this.item.setAmount(this.amount);
        if (VersionCheckers.getVersion() >= 13)
            meta.setCustomModelData(Integer.valueOf(this.customData));
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        this.item.setItemMeta(meta);
        if (this.glow)
            this.item.addEnchantment(Enchantment.ARROW_FIRE, 1);
        return this.item;
    }
}
