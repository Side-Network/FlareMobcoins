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
    FilesManager.FILES.initialization();
    Lib.LIB.libStart(plugin);
    Lib.LIB.setCustomPlaceholders(new PlaceholdersClass());
    Lib.LIB.setLocales(FilesManager.FILES.getLocale().getConfig());
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

  }

  /**
   * Stop method for the plugin - {@link JavaPlugin}
   *
   * @param plugin the plugin instance
   */
  public void stop(final TMMobCoinsPlugin plugin) {
    this.plugin = plugin;
    if(this.shopCommand != null)
      CommandsHandler.unRegisterBukkitCommand(this.shopCommand);
    CommandsHandler.unRegisterBukkitCommand(this.mobcoinsMainCommand);
  }

  /**
   * Initialize everything
   */
  private void init() {
    this.registerListener();
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
      new PlaceholderAPI().register();

    if(this.enabledMenu) {
      Lib.LIB.enableGUI();
    }
  }

  public void commandsSetup() {
    this.mobcoinsMainCommand = new MobcoinsCommand("mobcoins", "Mobcoins base command", FilesManager.FILES.getConfig().getConfig().getStringList("main_command_aliases"));
    CommandsHandler.registerCommand("mobcoins", this.mobcoinsMainCommand);
    //plugin.getCommand("tokens").setTabCompleter(new pluginCommand());

    if(!FilesManager.FILES.getConfig().getConfig().getBoolean("shop.settings.default_command")) {
      String usage = FilesManager.FILES.getConfig().getConfig().getString("shop.settings.open_command");
      this.shopCommand = new ShopMenuCommand(usage, "TMMobcoins shop custom command");
      CommandsHandler.registerCommand(usage, this.shopCommand);
    }
  }

  public void startStorage() {
    if(!FilesManager.FILES.getData().getConfig().contains("global_multiplier")) {
      FilesManager.FILES.getData().getConfig().set("global_multiplier", 1);
      FilesManager.FILES.getData().saveConfig();
    }
    if(FilesManager.FILES.getConfig().getConfig().getString("storage_type.type").equalsIgnoreCase("database")) {
      Lib.LIB.enableMySQL(
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.host"),
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.username"),
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.password"),
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.database"),
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.port"),
              FilesManager.FILES.getConfig().getConfig().getString("storage_type.driver")
      );
      Lib.LIB.getMySQL().sqlIO.createTable(FilesManager.FILES.getConfig().getConfig().getString("storage_type.connection.table"),
              "player VARCHAR(100), uuid VARCHAR(100), mobcoins DOUBLE(10,2), multiplier DOUBLE(10,2)");
    }
  }

  private void startLog() {
    Bukkit.getLogger().log(Level.INFO, "(( TMTOKENS )) Loading TMMobcoins");
    Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) Hooking into other plugins"));

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
      Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("(( TMTOKENS )) PlaceholderAPI is not on the server or not enabled! (( Placeholder support is disabled ))"));
    else Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) PlaceholderAPI is supported!"));

    if (Bukkit.getPluginManager().getPlugin("Vault") == null)
      Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("(( TMTOKENS )) Vault is not on the server or not enabled!  (( Economy support is disabled ))"));
    else Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) Vault is supported!"));

    Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) Checking version..."));
    new VersionCheckers(getPlugin(), 91848).getUpdate(version -> {
      if (getPlugin().getDescription().getVersion().equals(version)) {
        Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) Running latest build (" + version + ")"));
      } else {
        Bukkit.getLogger().log(Level.WARNING, ColorAPI.process("(( TMTOKENS )) Running an old build (" + getPlugin().getDescription().getVersion()
                + ") Latest build is (" + version + "). Please try to update to the last version!"));
      }
      Bukkit.getLogger().log(Level.INFO, ColorAPI.process("(( TMTOKENS )) Made with love by Romanians"));
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
    if(FilesManager.FILES.getConfig().getConfig().getBoolean("allow_bstats")) {
      bStatsMetrics metrics = new bStatsMetrics(getPlugin(), 14664);
    }
  }

}
