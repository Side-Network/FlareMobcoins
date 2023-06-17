package net.devtm.tmmobcoins.files;

import lombok.Getter;
import net.devtm.tmmobcoins.files.files.DropsFile;
import net.devtm.tmmobcoins.files.files.ConfigFile;
import net.devtm.tmmobcoins.files.files.DataFile;
import net.devtm.tmmobcoins.files.files.LocaleFile;
import net.devtm.tmmobcoins.TMMobCoinsPlugin;

@Getter
public enum FilesManager {
    
    ACCESS;

    private DataFile data;
    private LocaleFile locale;
    private ConfigFile config;
    private DropsFile drops;

    public void initialization() {
        this.data = new DataFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.locale = new LocaleFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.config = new ConfigFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
        this.drops = new DropsFile(TMMobCoinsPlugin.getPlugin(TMMobCoinsPlugin.class));
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
