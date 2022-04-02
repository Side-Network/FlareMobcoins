package net.tmmobcoins.lib.CBA;

import net.tmmobcoins.lib.Lib;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class ComponentBasedAction {

    private final List<CBAMethods> methodsList = new ArrayList<>();

    public void registerMethod(CBAMethods methods) {
        methodsList.add(0, methods);}

    public void unregisterMethod(CBAMethods methods) {methodsList.remove(methods);}

    public List<CBAMethods> getMethodsList() { return methodsList;}

    public void process(Player player, String component, Object obj) {
        for(CBAMethods methods : methodsList) {
            if(methods.getComponents().contains(component.substring(component.indexOf("[") + 1, component.indexOf("]")).toLowerCase(Locale.ROOT))){
                methods.process(player, component, obj); return;
            }
        }
        Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, "We did not find a component type for (" + component + ")");
    }

    public void processSpecified(CBAMethods method, Player player, String component, Object obj) {
        if(method.getComponents().contains(component.substring(component.indexOf("[") + 1, component.indexOf("]")).toLowerCase(Locale.ROOT))){
            method.process(player, component, obj); return;
        }
        Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, "We did not find a component type for (" + component + ")");
    }

}
