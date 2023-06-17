package net.devtm.tmmobcoins.files;

import net.devtm.tmmobcoins.files.files.*;
import net.devtm.tmmobcoins.TMMobCoinsPlugin;

public enum FilesManager {
    ACCESS;

    private DataFile data;
    private LocaleFile locale;
    private ConfigFile config;
    private DropsFile drops;
    private LogsFile logs;

    public DataFile getData() {
        return this.data;
    }

    public LogsFile getLogs() {
        return this.logs;
    }

    public LocaleFile getLocale() {
        return this.locale;
    }

    public ConfigFile getConfig() {
        return this.config;
    }

    public DropsFile getDrops() {
        return this.drops;
    }

    public void initialization() {
        this.data = new DataFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.locale = new LocaleFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.config = new ConfigFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.drops = new DropsFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.logs = new LogsFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        loadConfig();
    }

    public void reload() {
        this.data.reloadConfig();
        this.config.reloadConfig();
        this.locale.reloadConfig();
        this.drops.reloadConfig();
    }

    private void loadConfig() {
        this.data.saveDefaultConfig();
        this.config.saveDefaultConfig();
        this.locale.saveDefaultConfig();
        this.drops.saveDefaultConfig();
    }
}
