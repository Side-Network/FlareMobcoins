package net.tmmobcoins.lib.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class GUI implements Listener {

    public static HashMap<UUID, Menu> menuHolder = new HashMap<>();

    public GUI(JavaPlugin plugin) {
        runnable(plugin);
    }

    public void runnable(JavaPlugin plugin) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if(!menuHolder.isEmpty())
                    for(Menu menu : menuHolder.values()) {
                        menu.updateItems();
                    }
            }
        }.runTaskTimerAsynchronously(plugin, 10, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(menuHolder.get(event.getWhoClicked().getUniqueId()) == null) return;
        if(menuHolder.get(event.getWhoClicked().getUniqueId()).getInventory() == null) return;
        if(!menuHolder.containsKey(event.getWhoClicked().getUniqueId())) return;
        if(event.getInventory().equals(menuHolder.get(event.getWhoClicked().getUniqueId()).getInventory())) {
            event.setCancelled(true);
            if(menuHolder.get(event.getWhoClicked().getUniqueId()).menuContent.get(event.getRawSlot()) != null) {
                menuHolder.get(event.getWhoClicked().getUniqueId()).menuContent.get(event.getRawSlot()).onItemClick((Player) event.getWhoClicked(), event.getClick());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void inventoryClose(InventoryCloseEvent event) {
        if(menuHolder.get(event.getPlayer().getUniqueId()) == null) return;
        if(menuHolder.get(event.getPlayer().getUniqueId()).getInventory() == null) return;
        if(!menuHolder.containsKey(event.getPlayer().getUniqueId())) return;
        if(event.getInventory().equals(menuHolder.get(event.getPlayer().getUniqueId()).getInventory())) {
            if(menuHolder.get(event.getPlayer().getUniqueId()) != null) {
                menuHolder.remove(event.getPlayer().getUniqueId());
            }
        }
    }

}
