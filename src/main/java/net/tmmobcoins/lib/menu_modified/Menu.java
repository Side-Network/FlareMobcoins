package net.tmmobcoins.lib.menu_modified;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tmmobcoins.lib.StringUtils.StringTools;
import net.tmmobcoins.lib.base.ColorAPI;
import net.tmmobcoins.lib.menu_modified.item.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Menu {

    String inventoryID;
    Player holder;
    public HashMap<Integer, List<ItemHandler>> menuContent = new HashMap<>();
    public HashMap<Integer, ItemHandler> itemsInMenu = new HashMap<>();
    public HashMap<Integer, Integer> updateRate = new HashMap<>();
    public int slots;
    public Inventory inventory;
    private InventoryType type = null;

    public Menu(Player player, String name, int slots, String inventoryID) {
        this.holder = player;
        this.slots = slots;
        this.inventoryID = inventoryID;
        createInventory(name, slots);
    }

    public Menu(Player player, String name, InventoryType type, String inventoryID) {
        this.holder = player;
        this.type = type;
        this.inventoryID = inventoryID;
        createInventory(name, this.slots);
    }

    private void createInventory(String name, int slots) {
        if (this.type != null) {
            this.inventory = Bukkit.createInventory(null, this.type, ColorAPI.process(name));
        } else {
            this.inventory = Bukkit.createInventory(null, slots, ColorAPI.process(name));
        }
        getID();
    }

    public void deleteInventory() {
        GUI.menuHolder.remove(this.holder.getUniqueId());
        this.holder.closeInventory();
        this.menuContent.clear();
    }

    public void updateContent() {
        if (this.menuContent == null)
            return;

        for (Map.Entry<Integer, List<ItemHandler>> content : this.menuContent.entrySet()) {
            List<ItemHandler> menuItem = content.getValue();
            if (menuItem.size() == 1) {
                ItemHandler ih = menuItem.get(0);
                if (ih != null) {
                    if (Material.getMaterial(ih.material) == null) {
                        System.out.println("Couldn't get material for " + ih.material);
                        continue;
                    }

                    this.inventory.setItem(content.getKey(), ih.build());
                    this.itemsInMenu.put(content.getKey(), ih);
                }
                continue;
            }
            if (menuItem.size() > 1)
                for (ItemHandler ih : menuItem) {
                    if (ih != null && ih.view) {
                        this.inventory.setItem(content.getKey(), ih.build());
                        this.itemsInMenu.put(content.getKey(), ih);
                    }
                }
        }
    }

    public void updateSpecialItems() {
        if (this.menuContent == null)
            return;

        for (Map.Entry<Integer, List<ItemHandler>> content : this.menuContent.entrySet()) {
            try {
                List<ItemHandler> menuItem = content.getValue();
                if (menuItem.size() == 1) {
                    ItemHandler ih = menuItem.get(0);
                    if (ih.update) {
                        this.inventory.setItem(content.getKey(), ih.build());
                        this.itemsInMenu.put(content.getKey(), ih);
                    }
                    continue;
                }
                if (menuItem.size() > 1) {
                    if (this.updateRate.get(content.getKey()) == null)
                        setItemsInMenuInUpdates();
                    ItemHandler ih = content.getValue().get(this.updateRate.get(content.getKey()));
                    if (ih.update && ih.view) {
                        this.inventory.setItem(content.getKey(), ih.build());
                        this.itemsInMenu.put(content.getKey(), ih);
                    }
                }
            } catch (Exception e) {
                this.updateRate.put(content.getKey(), 0);
            }
        }
    }

    public void setItemsInMenuInUpdates() {
        for (Map.Entry<Integer, List<ItemHandler>> content : this.menuContent.entrySet()) {
            try {
                List<ItemHandler> menuItem = content.getValue();
                if (menuItem.size() > 1) {
                    if (!this.updateRate.containsKey(content.getKey()))
                        this.updateRate.put(content.getKey(), 0);
                    ItemHandler ih = content.getValue().get(this.updateRate.get(content.getKey()));
                    if (this.updateRate.get(content.getKey()) + 1 >= menuItem.size()) {
                        this.updateRate.put(content.getKey(), 0);
                        continue;
                    }
                    this.updateRate.put(content.getKey(), this.updateRate.get(content.getKey()) + 1);
                }
            } catch (Exception e) {
                this.updateRate.put(content.getKey(), 0);
            }
        }
    }

    private void getID() {
        GUI.menuHolder.put(this.holder.getUniqueId(), this);
    }

    public void assignItems(ItemHandler item) {
        if (!item.slot.isEmpty())
            if (item.slot.size() == 1) {
                if (item.slot.get(0) != -1) {
                    if (this.menuContent.containsKey(item.slot.get(0))) {
                        ItemHandler[] list = (ItemHandler[]) this.menuContent.get(item.slot.get(0)).toArray(new ItemHandler[this.menuContent.get(item.slot.get(0)).size() + 1]);
                        list[this.menuContent.get(item.slot.get(0)).size()] = item;
                        this.menuContent.put(item.slot.get(0), Arrays.asList(list));
                    } else {
                        this.menuContent.put(item.slot.get(0), Collections.singletonList(item));
                    }
                } else {
                    fillInventory(item);
                }
            } else {
                for (int i : item.slot) {
                    if (this.menuContent.get(i) != null) {
                        this.menuContent.get(i).add(item);
                        continue;
                    }
                    this.menuContent.put(i, StringTools.createList(item));
                }
            }
    }

    private void fillInventory(ItemHandler item) {
        for (int i = 0; i < this.slots; i++) {
            if (this.menuContent.get(i) != null) {
                this.menuContent.get(i).add(item);
            } else {
                this.menuContent.put(i, Collections.singletonList(item));
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
