package net.devtm.tmmobcoins;

import org.bukkit.plugin.java.JavaPlugin;

public class TMMobCoinsPlugin extends JavaPlugin {

    public MobcoinsAPI mobcoinsAPI;

    @Override
    public void onEnable() {
        TMMobCoins.PLUGIN.start(this);
    }

    @Override
    public void onDisable() {
        TMMobCoins.PLUGIN.stop(this);
    }
}
