package net.tmmobcoins.lib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

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



}
