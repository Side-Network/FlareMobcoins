package net.devtm.tmmobcoins.listener;

import net.devtm.tmmobcoins.files.FilesManager;
import net.devtm.tmmobcoins.util.MobCoinsPlayer;
import net.devtm.tmmobcoins.util.StorageAccess;
import net.tmmobcoins.lib.CBA.TMPL;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.base.VersionCheckers;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BasicListener implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) { StorageAccess.createAccount(e.getPlayer().getUniqueId()); }

    @EventHandler
    private void onPlayerKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        MobCoinsPlayer tp = StorageAccess.getAccount(event.getEntity().getKiller().getUniqueId());
        Configuration drops = FilesManager.FILES.getDrops().getConfig();
        double mobcoins;

        if(drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value"))
            mobcoins = Double.parseDouble(String.format("%.2f", generateNumber(drops, event) * tp.getMultiplier() * FilesManager.FILES.getData().getConfig().getDouble("global_multiplier")));
        else
            mobcoins = 0;

        if(drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement"))
            if(!new CodeArray().addConditions(drops.getString("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".requirement")).checkRequierment(event.getEntity().getKiller()))
                return;

        if (!FilesManager.FILES.getDrops().getConfig().contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT))) return;
        List<String> l = new ArrayList<>();

        for(String miniList : drops.getStringList("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_action")) {
            l.add(miniList.replace("%pl_mobcoins%", mobcoins + ""));
        }
        TMPL tmpl = new TMPL();
        tmpl.setCode(l);
        tmpl.process(event.getEntity().getKiller());

        if(!drops.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value")) return;
        tp.addMobcoins(mobcoins);
        tp.uploadPlayer();
    }

    @EventHandler
    private void playerFireworkDamage(EntityDamageByEntityEvent event) {
        if(VersionCheckers.getVersion() <= 9) return;
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    private double generateNumber(Configuration config, EntityDeathEvent event) {
        if(!config.contains("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value")) return 0;
        String str = config.getString("entity." + event.getEntity().getName().toUpperCase(Locale.ROOT) + ".drop_value");
        String[] definition = str.substring(0, str.length() - 1).split("\\(");
        String[] interval = definition[1].split(";");
        Random rand = new Random();
        switch (definition[0].toLowerCase(Locale.ROOT)) {
            case "random_number":
                return rand.nextInt((Integer.parseInt(interval[1]) - Integer.parseInt(interval[0])) + 1) + Integer.parseInt(interval[0]);
            case "random_decimal":
                return rand.nextDouble() * (rand.nextInt((Integer.parseInt(interval[1]) - Integer.parseInt(interval[0])) + 1) + Integer.parseInt(interval[0]));
        }
        return 0;
    }
}