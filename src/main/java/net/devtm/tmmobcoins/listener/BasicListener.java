package net.devtm.tmmobcoins.listener;

import net.devtm.tmmobcoins.API.MobCoinReceiveEvent;
import net.devtm.tmmobcoins.API.MobcoinsPlayer;
import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.service.ServiceHandler;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.base.VersionCheckers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;

public class BasicListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void mobcoinsReceiveEvent(MobCoinReceiveEvent event) {
        if (event.isCancelled() || event.getEntity() == null)
            return;

        event.getMobCoinsPlayer().giveMobcoins(event.getObtainedAmount(), false);
    }

    @EventHandler
    private void onPlayerKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        String configPath = ServiceHandler.SERVICE.getEventService().mobVerify(event);
        if (configPath == null)
            return;

        Player player = event.getEntity().getKiller();
        MobcoinsPlayer mobcoinsPlayer = ServiceHandler.SERVICE.getDataService().wrapPlayer(player.getUniqueId());
        if (mobcoinsPlayer == null) {
            TMMobCoins.PLUGIN.getPlugin().getLogger().log(Level.SEVERE, "The player profile could not be found!");
            return;
        }

        double amount = FilesManager.ACCESS.getDrops().getDropAmount(event.getEntityType());
        if (amount > 0) {
            amount = Double.parseDouble(String.format(
                    "%.2f",
                    mobcoinsPlayer.getMultiplier() * amount
            ));
        }

        MobCoinReceiveEvent eventMobcoins = new MobCoinReceiveEvent(player, mobcoinsPlayer, configPath, amount);
        Bukkit.getPluginManager().callEvent(eventMobcoins);
    }

    @EventHandler
    private void playerFireworkDamage(EntityDamageByEntityEvent event) {
        if (VersionCheckers.getVersion() <= 9)
            return;
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    private double generateNumber(@NotNull Configuration config, @NotNull String entityName) {
        final double defaultNumber = 0;
        final String entityConfigPath = String.format(
                "entity.%s.drop_value",
                entityName.toUpperCase(Locale.ROOT)
        );

        if (!config.contains(entityConfigPath)) {
            return defaultNumber;
        }

        String dropValue = config.getString(entityConfigPath);
        if (dropValue == null) {
            return defaultNumber;
        }

        String[] definition = dropValue.substring(0, dropValue.length() - 1).split("\\(");
        String[] interval = definition[1].split(";");
        Random rand = new Random();

        // When an end-user fills in a decimal number for random_number or random_decimal throw error in console that
        // a decimal number is not supported
        Integer parsedIntervalToIntegerOne = null;
        Integer parsedIntervalToIntegerTwo = null;
        try {
            parsedIntervalToIntegerOne = Integer.parseInt(interval[0]);
            parsedIntervalToIntegerTwo = Integer.parseInt(interval[1]);
        } catch (NumberFormatException e) {
            sendConsoleError(entityConfigPath);
            //e.printStackTrace();
        }

        if (parsedIntervalToIntegerOne == null || parsedIntervalToIntegerTwo == null) {
            return defaultNumber;
        }

        int randomNextInt = rand.nextInt(
                (parsedIntervalToIntegerTwo - parsedIntervalToIntegerOne) + 1
        ) + parsedIntervalToIntegerOne;

        switch (definition[0].toLowerCase(Locale.ROOT)) {
            case "random_number":
                return randomNextInt;
            case "random_decimal":
                return rand.nextDouble() * randomNextInt;
        }
        return defaultNumber;
    }

    private void sendConsoleError(String entityConfigPath) {
        TMMobCoins.PLUGIN.getPlugin().getServer().getConsoleSender().sendMessage(String.format(
                "[TMMobCoins] %sERROR: There is a not supported number used in your drops.yml config at %s%s%s. "
                        + "Only rounded numbers are supported for this option",
                ChatColor.RED,
                ChatColor.GOLD,
                entityConfigPath,
                ChatColor.RED
        ));
    }
}