package net.tmmobcoins.lib.menu;

import net.tmmobcoins.lib.base.ColorAPI;
import net.tmmobcoins.lib.menu.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Menu {

    Player holder;
    public HashMap<Integer, ItemHandler> menuContent = new HashMap<>();
    public int slots;
    public Inventory inventory;

    public Menu(Player player, String name, int slots) {
        this.holder = player;
        this.slots = slots;
        createInventory(name, slots);
    }

    private void createInventory(String name, int slots) {
        inventory = Bukkit.createInventory(null, slots, ColorAPI.process(name));
        getID();

    }

    public void deleteInventory() {
        GUI.menuHolder.remove(holder.getUniqueId());
        holder.closeInventory();
        menuContent.clear();
    }

    public void updateInventory() {
        for (Map.Entry<Integer, ItemHandler> integerItemHandlerEntry : menuContent.entrySet()) {
            ItemHandler handler = (ItemHandler) ((Map.Entry) integerItemHandlerEntry).getValue();
            if(handler.view)
                inventory.setItem((Integer) ((Map.Entry) integerItemHandlerEntry).getKey(), handler.build());
            }
    }

    public void updateItems() {
        for (Map.Entry<Integer, ItemHandler> integerItemHandlerEntry : menuContent.entrySet()) {
            ItemHandler handler = (ItemHandler) ((Map.Entry) integerItemHandlerEntry).getValue();
            if (handler.update)
                if(handler.item != null) {
                    inventory.setItem((Integer) ((Map.Entry) integerItemHandlerEntry).getKey(), handler.build());
                }
        }
    }

    private void getID() {
        GUI.menuHolder.put(holder.getUniqueId(), this);
    }

    public void assignItems(ItemHandler item) {
        if(item.slot.size() == 1) {
            if(item.slot.get(0) != -1) {
                menuContent.put(item.slot.get(0), item);
            } else {
                fillInventory(item);
            }
        } else if (item.slot.size() > 1) {
            for(int i : item.slot) {
                menuContent.put(i, item);
            }
        }
    }

    private void fillInventory(ItemHandler item) {
        for(int i = 0; i < slots; i++) {
            menuContent.put(i, item);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

}
