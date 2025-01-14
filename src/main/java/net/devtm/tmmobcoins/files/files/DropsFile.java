package net.devtm.tmmobcoins.files.files;

import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.TMMobCoinsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DropsFile {

    private final TMMobCoinsPlugin plugin;
    private final String file = "drops.yml";
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    private final Map<EntityType, Double> dropAmounts = new HashMap<>();

    public DropsFile(TMMobCoinsPlugin plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
        reloadConfig();
    }

    public double getDropAmount(EntityType entityType) {
        return dropAmounts.getOrDefault(entityType, 0.0);
    }

    /**
     * This method is used to reload the config
     */
    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), file);
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource(file);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }

        dropAmounts.clear();
        if (dataConfig.isConfigurationSection("entity")) {
            //noinspection ConstantConditions
            for (String entityName : dataConfig.getConfigurationSection("entity").getKeys(false)) {
                try {
                    EntityType entityType = EntityType.valueOf(entityName);
                    double val = dataConfig.getDouble("entity." + entityName + ".drop_value", 0.0);
                    dropAmounts.put(entityType, val);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to load entity drop " + entityName);
                }
            }
        }
    }

    /**
     * This method is used to get the config file
     */
    public FileConfiguration getConfig() {
        if (this.dataConfig == null) {
            reloadConfig();
        }
        return this.dataConfig;
    }

    /**
     * This method is used to save the file
     */
    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "Failed to save: " + file);
        }
    }

    /**
     * This method is used to create the file for the first time the file
     */
    public void saveDefaultConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), file);
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource(file, false);
        }
    }
}
