package net.tmmobcoins.lib.CBA.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.tmmobcoins.lib.CBA.CBAMethods;
import net.tmmobcoins.lib.Lib;
import net.tmmobcoins.lib.base.ColorAPI;
import net.tmmobcoins.lib.base.MessageHandler;
import net.tmmobcoins.lib.base.VersionCheckers;
import net.tmmobcoins.lib.menu_modified.GUI;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class DefaultCBA implements CBAMethods {

    List<String> comp = Arrays.asList("console", "player", "message", "broadcast", "exit", "firework", "title", "actionbar", "particle");

    @Override
    public void process(Player player, String component, Object obj) {
        String actionContent = "";
        if (component.split("]").length > 1)
            actionContent = component.split("]")[1].replaceFirst("^ *", "");

        switch (component.substring(component.indexOf("[") + 1, component.indexOf("]")).toLowerCase(Locale.ROOT)) {
            case "console" -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MessageHandler.chat(actionContent).placeholderAPI(player).addColors().toString());
                return;
            }
            case "player" -> {
                player.performCommand(MessageHandler.chat(actionContent).placeholderAPI(player).addColors().toString());
                return;
            }
            case "message" -> {
                player.sendMessage(MessageHandler.chat(actionContent).placeholderAPI(player).addColors().toString());
                return;
            }
            case "broadcast" -> {
                for (Player pl : Bukkit.getOnlinePlayers())
                    pl.sendMessage(MessageHandler.chat(actionContent).placeholderAPI(player).addColors().toString());
                return;
            }
            case "exit" -> {
                if (Lib.LIB.getGui() != null && GUI.menuHolder.containsKey(player.getUniqueId()))
                    GUI.menuHolder.get(player.getUniqueId()).deleteInventory();
                return;
            }
            case "title" -> {
                /* Titles: [TITLE] title;subtitle;in;stay;out */
                /* Titles: [TITLE] title;subtitle */
                if (VersionCheckers.getVersion() >= 9) {
                    try {
                        String[] args = actionContent.split(";");
                        if (args.length > 2) {
                            player.sendTitle(ColorAPI.process(args[0]), ColorAPI.process(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                        } else {
                            player.sendTitle(ColorAPI.process(args[0]), ColorAPI.process(args[1]), 10, 20, 10);
                        }
                    } catch (Exception e) {
                        Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, "We catch an error for (" + component + ")");
                    }
                }
                return;
            }
            case "actionbar" -> {
                /* Titles: [TITLE] title;subtitle;in;stay;out */
                /* Titles: [TITLE] title;subtitle */
                if (VersionCheckers.getVersion() >= 9) {
                    try {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorAPI.process(actionContent)));
                    } catch (Exception e) {
                        Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, "We catch an error for (" + component + ")");
                    }
                }
                return;
            }
            case "firework" -> {
                actionContent = actionContent.replace("%pl_world%", player.getLocation().getWorld().getName())
                        .replace("%pl_x%", String.valueOf(player.getLocation().getX()))
                        .replace("%pl_y%", String.valueOf(player.getLocation().getY()))
                        .replace("%pl_z%", String.valueOf(player.getLocation().getZ()));
                String[] args = actionContent.split(";");
                Location loc = new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]) + 1, Double.parseDouble(args[3]));
                //XParticle.sphere(2,100, ParticleDisplay.display(), XParticle.getParticle(args[4])));
                FireworkEffect.Builder builder = FireworkEffect.builder();
                FireworkEffect effect = builder.flicker(true).trail(true).with(FireworkEffect.Type.BURST).withColor(org.bukkit.Color.RED).withFade(org.bukkit.Color.NAVY).build();
                Firework firework = loc.getWorld().spawn(loc, Firework.class);
                FireworkMeta fwm = firework.getFireworkMeta();
                firework.setMetadata("nodamage", new FixedMetadataValue(Lib.LIB.getPlugin(), true));
                fwm.clearEffects();
                fwm.addEffect(effect);
                firework.setFireworkMeta(fwm);
                firework.detonate();
                return;
            }
        }

        Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, ColorAPI.process("&cTMPL Error on a command because ACTION type not exists on command! Command: &f"));
    }

    @Override
    public List<String> getComponents() {
        return comp;
    }
}
