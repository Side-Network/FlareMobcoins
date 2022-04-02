package net.tmmobcoins.lib;

import lombok.Getter;
import net.tmmobcoins.lib.CBA.ComponentBasedAction;
import net.tmmobcoins.lib.base.CustomPlaceholders;
import net.tmmobcoins.lib.database.MySQL;
import net.tmmobcoins.lib.menu.GUI;
import net.tmmobcoins.lib.CBA.utils.DefaultCBA;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum Lib {
    LIB;

    private Configuration locale;
    private JavaPlugin plugin;
    private GUI gui = null;
    private ComponentBasedAction componentBasedAction = null;
    private MySQL mySQL = null;
    private CustomPlaceholders customPlaceholders;

    public Lib libStart(JavaPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public Lib libStop() {

        return this;
    }

    public Lib setLocales(Configuration locale) {
        this.locale = locale;
        return this;
    }

    public void enableCBA() {
        componentBasedAction = new ComponentBasedAction();
        componentBasedAction.registerMethod(new DefaultCBA());

    }

    public void enableGUI () {
        gui = new GUI(plugin);
        gui.runnable(plugin);
        Bukkit.getPluginManager().registerEvents(gui, plugin);
    }

    public void enableMySQL(String host, String user, String password, String database, String port, String driver) {
        this.mySQL = new MySQL(host, user, password, database, port, driver);
        mySQL.connect();
    }

    public void setCustomPlaceholders(CustomPlaceholders customPlaceholders) {
        this.customPlaceholders = customPlaceholders;
    }
}
