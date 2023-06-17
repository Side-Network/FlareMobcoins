package net.tmmobcoins.lib;

import net.tmmobcoins.lib.CBA.ComponentBasedAction;
import net.tmmobcoins.lib.CBA.utils.DefaultCBA;
import net.tmmobcoins.lib.base.CustomPlaceholders;
import net.tmmobcoins.lib.database.SQLActions;
import net.tmmobcoins.lib.menu_modified.GUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public enum Lib {
    LIB;

    private CustomPlaceholders customPlaceholders;

    private SQLActions sqlActions;

    private ComponentBasedAction componentBasedAction;

    private GUI gui;

    private JavaPlugin plugin;

    private Configuration locale;

    Lib() {
        this.gui = null;
        this.componentBasedAction = null;
        this.sqlActions = null;
    }

    public Configuration getLocale() {
        return this.locale;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public GUI getGui() {
        return this.gui;
    }

    public ComponentBasedAction getComponentBasedAction() {
        return this.componentBasedAction;
    }

    public SQLActions getSql() {
        return sqlActions;
    }

    public CustomPlaceholders getCustomPlaceholders() {
        return this.customPlaceholders;
    }

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
        this.componentBasedAction = new ComponentBasedAction();
        this.componentBasedAction.registerMethod(new DefaultCBA());
    }

    public void enableGUI() {
        this.gui = new GUI(this.plugin);
        this.gui.runnable(this.plugin);
        Bukkit.getPluginManager().registerEvents(this.gui, this.plugin);
    }

    public void enableMySQL(String host, String user, String password, String database, String port) {
        this.sqlActions = new SQLActions(host, user, password, database, port);
    }

    public void disableMySQL() {
        sqlActions.close();
    }

    public void setCustomPlaceholders(CustomPlaceholders customPlaceholders) {
        this.customPlaceholders = customPlaceholders;
    }
}
