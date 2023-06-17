package net.devtm.tmmobcoins.listener;

import net.devtm.tmmobcoins.API.MobCoinReceiveEvent;
import net.devtm.tmmobcoins.TMMobCoins;
import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.util.MobCoinsPlayer;
import net.devtm.tmmobcoins.util.StorageAccess;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.base.VersionCheckers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BasicListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        StorageAccess.createAccount(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void mobcoinsReceiveEvent(MobCoinReceiveEvent event) {
        if (event.isCancelled())
            return;
        if (event.getEntity() == null)
            return;
        Configuration drops = FilesManager.ACCESS.getDrops().getConfig();

        if (!FilesManager.ACCESS.getDrops().getConfig().contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT)))
            return;
        List<String> l = new ArrayList<>();

        for (String miniList : drops.getStringList("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_action")) {
            l.add(miniList.replace("%pl_mobcoins%", event.getObtainedAmount() + ""));
        }
        TMPL tmpl = new TMPL();
        tmpl.setCode(l);
        tmpl.process(event.getPlayer());

        if (!drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value")) return;
        event.getMobCoinsPlayer().addMobcoins(event.getObtainedAmount());
        event.getMobCoinsPlayer().uploadPlayer();
    }

    @EventHandler
    private void onPlayerKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;
        MobCoinsPlayer tp = StorageAccess.getAccount(event.getEntity().getKiller().getUniqueId());
        Configuration drops = FilesManager.ACCESS.getDrops().getConfig();
        Entity entity = event.getEntity();
        Player player = event.getEntity().getKiller();
        double mobcoins;

        if(drops.contains("entity." + entity.getName().toUpperCase(Locale.ROOT) + ".drop_value"))
            mobcoins = Double.parseDouble(String.format("%.2f", generateNumber(drops, entity.getName()) * tp.getMultiplier() * FilesManager.ACCESS.getData().getConfig().getDouble("global_multiplier")));
        else
            mobcoins = 0;

        MobCoinReceiveEvent eventMobcoins = new MobCoinReceiveEvent(player, tp, entity, mobcoins);

        if(drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement")) {
            if (!new CodeArray().addConditions(drops.getString("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement")).checkRequierment(event.getEntity().getKiller()))
                eventMobcoins.setCancelled(true);
        }

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