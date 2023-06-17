package net.devtm.tmmobcoins;

import net.devtm.tmmobcoins.command.MobcoinsCommand;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.listener.BasicListener;
import net.devtm.tmmobcoins.listener.ShopCommand;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.devtm.tmmobcoins.util.CustomCBA;
import net.devtm.tmmobcoins.util.PlaceholderAPI;
import net.devtm.tmmobcoins.util.PlaceholdersClass;
import net.devtm.tmmobcoins.util.Utils;
import net.tmmobcoins.lib.Lib;
import net.tmmobcoins.lib.base.ColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public enum TMMobCoins {

    PLUGIN;

    private final boolean enabledMenu = true;
    private TMMobCoinsPlugin plugin;

    public TMMobCoinsPlugin getPlugin() {
        return plugin;
    }

    public boolean isEnabledMenu() {
        return enabledMenu;
    }

    public void start(final TMMobCoinsPlugin plugin) {
        FilesManager.ACCESS.initialization();
        Lib.LIB.libStart(plugin);
        Lib.LIB.setCustomPlaceholders(new PlaceholdersClass());
        Lib.LIB.setLocales(FilesManager.ACCESS.getLocale().getConfig());
        Lib.LIB.enableCBA();
        Lib.LIB.getComponentBasedAction().registerMethod(new CustomCBA());
        this.plugin = plugin;
        startStorage();
        assert plugin != null : "Something went wrong! Plugin was null.";
        init();
        startLog();
        commandsSetup();

        ServiceHandler.SERVICE.getLoggerService().fileSetup();
        ServiceHandler.SERVICE.getDataService().reloadDataService();
        Utils.UTILS.reloadUtils();
    }

    /**
     * Stop method for the plugin - {@link JavaPlugin}
     *
     * @param plugin the plugin instance
     */
    public void stop(final TMMobCoinsPlugin plugin) {
        this.plugin = plugin;
        ServiceHandler.SERVICE.getDataService().saveAll();
        Lib.LIB.disableMySQL();
        stopLog();
    }

    /**
     * Initialize everything
     */
    private void init() {
        this.registerListener();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderAPI().register();

        if (this.enabledMenu)
            Lib.LIB.enableGUI();
    }

    public void commandsSetup() {
        //noinspection ConstantConditions
        plugin.getCommand("tmobcoins").setExecutor(new MobcoinsCommand());
    }

    public void startStorage() {
        if (!FilesManager.ACCESS.getData().getConfig().contains("global_multiplier")) {
            FilesManager.ACCESS.getData().getConfig().set("global_multiplier", 1);
            FilesManager.ACCESS.getData().saveConfig();
        }
        if (FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type").equalsIgnoreCase("mysql")) {
            Lib.LIB.enableMySQL(
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.host"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.username"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.password"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.database"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.port")
            );
            Lib.LIB.getSql().createTable(FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table"),
                    "player VARCHAR(100), uuid VARCHAR(100), mobcoins DOUBLE(10,2), multiplier DOUBLE(10,2)");
        }
    }

    private void startLog() {
        plugin.getLogger().log(Level.INFO, ColorAPI.process("Loading TMMobcoins"));
        plugin.getLogger().log(Level.INFO, ColorAPI.process("Hooking into other plugins"));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            plugin.getLogger().log(Level.INFO, ColorAPI.process("PlaceholderAPI is not on the server or not enabled! (( Placeholder support is disabled ))"));
        else
            plugin.getLogger().log(Level.INFO, ColorAPI.process("PlaceholderAPI is supported!"));

        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
            plugin.getLogger().log(Level.WARNING, ColorAPI.process("Vault is not on the server or not enabled!  (( Economy support is disabled ))"));
        else
            plugin.getLogger().log(Level.INFO, ColorAPI.process("Vault is supported!"));
    }

    private void stopLog() {
        this.plugin.getLogger().log(Level.INFO, ColorAPI.process("Disabling TMMobcoins v" + this.plugin.getDescription().getVersion()));
    }

    /**
     * Register all listener
     */
    private void registerListener() {
        final Listener[] listeners = new Listener[]{
                new BasicListener(),
                new ShopCommand(),
        };

        Arrays.stream(listeners)
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));
    }
}
