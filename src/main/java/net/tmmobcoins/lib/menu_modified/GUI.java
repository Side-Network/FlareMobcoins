package net.tmmobcoins.lib.menu_modified;

import java.util.HashMap;
import java.util.UUID;
import net.devtm.tmmobcoins.service.ServiceHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GUI implements Listener {
    int tick = 0;

    public static HashMap<UUID, Menu> menuHolder = new HashMap<>();

    public GUI(JavaPlugin plugin) {
        runnable(plugin);
    }

    public void runnable(JavaPlugin plugin) {
        (new BukkitRunnable() {
            public void run() {
                if (!GUI.menuHolder.isEmpty())
                    for (Menu menu : GUI.menuHolder.values()) {
                        menu.updateSpecialItems();
                        if (GUI.this.tick >= 20) {
                            menu.setItemsInMenuInUpdates();
                            GUI.this.tick = 0;
                            continue;
                        }
                        GUI.this.tick++;
                    }
            }
        }).runTaskTimerAsynchronously(plugin, 10L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Menu menu = menuHolder.get(event.getWhoClicked().getUniqueId());
        if (menu == null)
            return;
        if (menu.getInventory() == null)
            return;

        if (event.getInventory().equals(menu.getInventory())) {
            event.setCancelled(true);
            if (menu.itemsInMenu.get(event.getRawSlot()) != null &&
                    ServiceHandler.SERVICE.getMenuService().canBuyItem(
                            (Player)event.getWhoClicked(),
                            menu.inventoryID,
                            menu.itemsInMenu.get(event.getRawSlot()).itemName
                    )
            ) {
                menu.itemsInMenu.get(event.getRawSlot()).onItemClick(
                        (Player) event.getWhoClicked(),
                        event.getClick(),
                        menu.inventoryID,
                        menu.itemsInMenu.get(event.getRawSlot()).itemName
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void inventoryClose(InventoryCloseEvent event) {
        if (menuHolder.get(event.getPlayer().getUniqueId()) == null)
            return;
        if (menuHolder.get(event.getPlayer().getUniqueId()).getInventory() == null)
            return;
        if (!menuHolder.containsKey(event.getPlayer().getUniqueId()))
            return;
        if (event.getInventory().equals(menuHolder.get(event.getPlayer().getUniqueId()).getInventory()) &&
                menuHolder.get(event.getPlayer().getUniqueId()) != null)
            menuHolder.remove(event.getPlayer().getUniqueId());
    }
}
