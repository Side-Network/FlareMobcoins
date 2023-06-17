package net.devtm.tmmobcoins;

import lombok.Getter;
import net.devtm.tmmobcoins.command.ShopMenuCommand;
import net.devtm.tmmobcoins.command.MobcoinsCommand;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.listener.BasicListener;
import net.devtm.tmmobcoins.util.PlaceholderAPI;
import net.devtm.tmmobcoins.util.PlaceholdersClass;
import net.devtm.tmmobcoins.util.Utils;
import net.tmmobcoins.lib.Lib;
import net.tmmobcoins.lib.base.ColorAPI;
import net.tmmobcoins.lib.base.VersionCheckers;
import net.tmmobcoins.lib.base.bStatsMetrics;
import net.tmmobcoins.lib.utils.CommandsHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

@Getter
public enum TMMobCoins {

    PLUGIN;

    private TMMobCoinsPlugin plugin;
    private Utils utils;
    private final boolean enabledMenu = true;

    private Command shopCommand;
    private Command mobcoinsMainCommand;

    public void start(final TMMobCoinsPlugin plugin) {
        FilesManager.ACCESS.initialization();
        Lib.LIB.libStart(plugin);
        Lib.LIB.setCustomPlaceholders(new PlaceholdersClass());
        Lib.LIB.setLocales(FilesManager.ACCESS.getLocale().getConfig());
        Lib.LIB.enableCBA();
        this.plugin = plugin;
        this.utils = new Utils();
        startStorage();
        assert plugin != null : "Something went wrong! Plugin was null.";
        this.init();
        startLog();
        usebStats();
        commandsSetup();

        /* Make the tokens object */

        plugin.mobcoinsAPI = new MobcoinsAPI();

        /* init the shop runner */
        if (FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.shop_type").equalsIgnoreCase("rotating")) {
            utils.runnable(plugin);
            if (FilesManager.ACCESS.getData().getConfig().getStringList("refresh_data.items_in_storage.normal").isEmpty())
                utils.regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "normal");
            if (FilesManager.ACCESS.getData().getConfig().getStringList("refresh_data.items_in_storage.special").isEmpty())
                utils.regenerateItems(FilesManager.ACCESS.getConfig().getConfig(), "special");
        }
    }

    /**
     * Stop method for the plugin - {@link JavaPlugin}
     *
     * @param plugin the plugin instance
     */
    public void stop(final TMMobCoinsPlugin plugin) {
        this.plugin = plugin;
        if (this.shopCommand != null) {}
        //CommandsHandler.unRegisterBukkitCommand(this.shopCommand);
        //CommandsHandler.unRegisterBukkitCommand(this.mobcoinsMainCommand);
    }

    /**
     * Initialize everything
     */
    private void init() {
        this.registerListener();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
            System.out.println("registering PAPI");
        }

        if (this.enabledMenu) {
            Lib.LIB.enableGUI();
        }
    }

    public void commandsSetup() {
        this.mobcoinsMainCommand = new MobcoinsCommand("tmobcoins", "Mobcoins base command", FilesManager.ACCESS.getConfig().getConfig().getStringList("main_command_aliases"));
        CommandsHandler.registerCommand("mobcoins", this.mobcoinsMainCommand);
        //plugin.getCommand("tokens").setTabCompleter(new pluginCommand());

        if (!FilesManager.ACCESS.getConfig().getConfig().getBoolean("shop.settings.default_command")) {
            String usage = FilesManager.ACCESS.getConfig().getConfig().getString("shop.settings.open_command");
            this.shopCommand = new ShopMenuCommand(usage, "TMMobcoins shop custom command");
            CommandsHandler.registerCommand(usage, this.shopCommand);
        }
    }

    public void startStorage() {
        if (!FilesManager.ACCESS.getData().getConfig().contains("global_multiplier")) {
            FilesManager.ACCESS.getData().getConfig().set("global_multiplier", 1);
            FilesManager.ACCESS.getData().saveConfig();
        }
        if (FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.type").equalsIgnoreCase("database")) {
            Lib.LIB.enableMySQL(
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.host"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.username"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.password"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.database"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.port"),
                    FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.driver")
            );
            Lib.LIB.getMySQL().sqlIO.createTable(FilesManager.ACCESS.getConfig().getConfig().getString("storage_type.connection.table"),
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
            Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("Vault is not on the server or not enabled!  (( Economy support is disabled ))"));
        else
            plugin.getLogger().log(Level.INFO, ColorAPI.process("Vault is supported!"));

        plugin.getLogger().log(Level.INFO, ColorAPI.process("Checking version..."));
        new VersionCheckers(getPlugin(), 91848).getUpdate(version -> {
            if (getPlugin().getDescription().getVersion().equals(version)) {
                plugin.getLogger().log(Level.INFO, ColorAPI.process("Running latest build (" + version + ")"));
            } else {
                Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("Running an old build (" + getPlugin().getDescription().getVersion()
                        + ") Latest build is (" + version + "). Please try to update to the last version!"));
            }
            plugin.getLogger().log(Level.INFO, ColorAPI.process("Made with love by Romanians"));
        });
    }

    /**
     * Register all listener
     */
    private void registerListener() {
        final Listener[] listeners = new Listener[]{
                new BasicListener()
        };

        Arrays.stream(listeners)
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));
    }

    private void usebStats() {
        if (FilesManager.ACCESS.getConfig().getConfig().getBoolean("allow_bstats")) {
            bStatsMetrics metrics = new bStatsMetrics(getPlugin(), 14664);
        }
    }

}
