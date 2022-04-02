package net.tmmobcoins.lib.base;

import net.tmmobcoins.lib.Lib;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class VersionCheckers {

    private final JavaPlugin plugin;
    private final int resourceId;

    public VersionCheckers(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public static int getVersion() {
        String version = Bukkit.getVersion();
        Validate.notEmpty(version, "[[ API ]] (( ERROR )) The server version is unknown or not a major version.");

        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        // 1.13.2, 1.14.4, etc...
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) version = version.substring(0, lastDot);

        return Integer.parseInt(version.substring(2));
    }

    public void getUpdate(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(is)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                Lib.LIB.getPlugin().getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cFailed to check the version &f" + exception.getMessage()));
            }
        });
    }
}
