package net.tmmobcoins.lib.CBA;

import org.bukkit.entity.Player;

import java.util.List;

public interface CBAMethods {

    String name = null;

    List<String> components = null;

    /**
     * You can execute your own ComponentsBasedActions components. Simply put the code and then you can use the object argument (ex: an event)
     * @param player Player to execute the action
     * @param component The string with the component
     * @param obj An object that can do a lot of stuff
     */
    void process(Player player, String component, Object obj);

    List<String> getComponents();


}
