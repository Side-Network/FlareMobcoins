package net.tmmobcoins.lib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandsHandler {

    public static void registerCommand(String usage, Command cmd){
        try{
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(usage, cmd);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unRegisterBukkitCommand(Command cmd) {
        try {
            Field cMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            cMap.setAccessible(true);
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            ((Map)knownCommands.get((Object)((SimpleCommandMap)cMap.get((Object)Bukkit.getServer())))).remove(cmd.getName());
            cmd.unregister((CommandMap)cMap.get((Object)Bukkit.getServer()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
