package net.tmmobcoins.lib.CBA.utils;

import net.tmmobcoins.lib.Lib;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeArray {

    public CodeArray() {
    }

    public List<String> conditions = new ArrayList<>();
    public List<String> commands = new ArrayList<>();
    public List<String> fallCommands = new ArrayList<>();

    ClickType clickType;

    public CodeArray addConditions(String condition) {
        this.conditions.add(condition);
        return this;
    }
    public void addFallCommands(String fallCommands) {
        this.fallCommands.add(fallCommands);
    }

    public void addCommands(String commands) {
        this.commands.add(commands);
    }


    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public void setFallCommands(List<String> fallCommands) {
        this.fallCommands = fallCommands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean checkRequierment(Player player) {
        boolean test = true;
        for(String s : conditions) {
            if(s.toLowerCase().contains("require")) {
                Matcher m = Pattern.compile("\\((.*?)\\)").matcher(s);
                m.find();
                test = new RequireParser().provideClickType(clickType).getResult(m.group(1).split(";"), player) && test;
            }
        }
        if(test) {
            runCommands(player);
        } else {
            runFallCommands(player);
        }
        return test;
    }

    public void runCommands(Player player) {
        if(!commands.isEmpty()) {
            for(String s : commands) {
                if (s.contains("[") && s.contains("]")) {
                    Lib.LIB.getComponentBasedAction().process(player, s, null);
                }
            }
        }
    }
    public void runFallCommands(Player player) {
        if(!fallCommands.isEmpty()) {
            for(String s : fallCommands) {
                Lib.LIB.getComponentBasedAction().process(player, s, null);
            }
        }
    }

    public void provideClickType(ClickType clickType) {
        this.clickType = clickType;
    }

}
