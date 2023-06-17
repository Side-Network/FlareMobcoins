package net.devtm.tmmobcoins.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.tmmobcoins.lib.CBA.CBAMethods;
import net.tmmobcoins.lib.Lib;
import net.tmmobcoins.lib.base.ColorAPI;
import org.bukkit.entity.Player;

public class CustomCBA implements CBAMethods {

    List<String> comp = Arrays.asList("open_menu", "mobcoins_give", "mobcoins_set", "mobcoins_remove");

    public void process(Player player, String component, Object obj) {
        String actionContent = "";
        if ((component.split("]")).length > 1)
            actionContent = component.split("]")[1].replaceFirst("^ *", "");
        switch (component.substring(component.indexOf("[") + 1, component.indexOf("]")).toLowerCase(Locale.ROOT)) {
            case "open_menu":
                player.closeInventory();
                ServiceHandler.SERVICE.getMenuService().openMenu(player, actionContent + ".yml");
                return;
            case "mobcoins_give":
                ServiceHandler.SERVICE.getDataService().wrapPlayer(player.getUniqueId()).giveMobcoins(Double.parseDouble(actionContent), true);
                return;
            case "mobcoins_set":
                ServiceHandler.SERVICE.getDataService().wrapPlayer(player.getUniqueId()).setMobcoins(Double.parseDouble(actionContent));
                return;
            case "mobcoins_remove":
                ServiceHandler.SERVICE.getDataService().wrapPlayer(player.getUniqueId()).removeMobcoins(Double.parseDouble(actionContent));
                return;
        }
        Lib.LIB.getPlugin().getLogger().log(Level.INFO, ColorAPI.process("&7(( &cERROR &7)) &cTMPL Error on a command because ACTION type not exists on command! Command: &f"));
    }

    public List<String> getComponents() {
        return this.comp;
    }
}
