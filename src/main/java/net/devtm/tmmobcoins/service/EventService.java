package net.devtm.tmmobcoins.service;

import net.devtm.tmmobcoins.files.FilesManager;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.List;

public class EventService {

    public String mobVerify(EntityDeathEvent event) {
        if (FilesManager.ACCESS.getDrops().getConfig().contains("entity." + event.getEntity().getType()))
            return "entity." + event.getEntity().getType();
        if (FilesManager.ACCESS.getDrops().getConfig().contains("entity.HOSTILE") && this.hostileMobs.contains(event.getEntity().getType().toString()))
            return "entity.HOSTILE";
        if (FilesManager.ACCESS.getDrops().getConfig().contains("entity.PASSIVE") && !this.hostileMobs.contains(event.getEntity().getType().toString()))
            return "entity.PASSIVE";
        return null;
    }

    List<String> hostileMobs = Arrays.asList("BLAZE", "CREEPER", "DROWNED", "ELDER_GUARDIAN", "ENDERMITE", "EVOKER", "GHAST", "GIANT", "GUARDIAN", "HOGLIN",
            "HUSK", "ILLUSIONER", "MAGMA_CUBE", "PHANTOM", "PIGLIN_BRUTE", "PIGLIN", "PILLAGER", "RAVAGER", "SHULKER", "SILVERFISH",
            "SKELETON", "SLIME", "SPIDER", "STRAY", "VEX", "VINDICATOR", "WITCH", "WITHER", "WITHER_SKELETON", "ZOGLIN",
            "ZOMBIE", "ZOMBIE_VILLAGER");
}
