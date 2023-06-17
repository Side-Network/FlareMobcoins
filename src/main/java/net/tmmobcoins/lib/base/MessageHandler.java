package net.tmmobcoins.lib.base;

import me.clip.placeholderapi.PlaceholderAPI;
import net.tmmobcoins.lib.Lib;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class MessageHandler {

    private String text;

    /*
     * @param text
     */

    public MessageHandler(String text) {
        this.text = text;
    }

    public static MessageHandler chat(String text) {
        return new MessageHandler(text);
    }

    public static MessageHandler message(String text) {
        return new MessageHandler(Lib.LIB.getLocale().getString(text));
    }

    public MessageHandler replace(String field, String value) {
        this.text = StringUtils.replace(this.text, field, value);
        return this;
    }

    public MessageHandler addColors() {
        this.text = ColorAPI.process(text);
        return this;
    }

    public MessageHandler placeholderAPI(Object p) {
        Player player = null;
        try {
            player = (Player) p;
        } catch (Exception e) {
            Lib.LIB.getPlugin().getLogger().log(Level.WARNING, ColorAPI.process("&7(( &bWARN &7)) &fPlaceholderAPI failed, for the console, you can only use placeholderAPI by player. Except placeholders not to work"));
            return this;
        }
        if(player == null) return this;
        this.text = this.text.replace("%pl_player%", player.getName());

        this.text = Lib.LIB.getCustomPlaceholders().process(text, player);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.text = PlaceholderAPI.setPlaceholders((Player) player, this.text);
        }
        return this;
    }


    public MessageHandler prefix() {
        return replace("%pl_prefix%", Lib.LIB.getLocale().getString("prefix"));
    }

    @Override
    public String toString() {
        return this.text;
    }

    public String toStringColor() {
        this.text = ColorAPI.process(text);
        return this.text;
    }
}
