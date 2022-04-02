package net.tmmobcoins.lib.CBA;

import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.CBA.utils.CodeCompiler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TMPL {

    private List<String> codeList = new ArrayList<>();

    ClickType clickType;

    public TMPL() {}

    public void setCode(String s) {
        codeList.add(s);
    }

    public void setCode(List<String> codeList) {
        this.codeList = codeList;
    }

    public void process(Player player) {
        HashMap<Integer, CodeArray> codeCompilerOutput;
        if(!codeList.isEmpty()) {
            codeCompilerOutput = new CodeCompiler().process(codeList);
            for(CodeArray s : codeCompilerOutput.values()) {
                s.provideClickType(clickType);
                s.checkRequierment(player);
            }
        }
    }

    public void provideClickType(ClickType clickType) {
        this.clickType = clickType;
    }

}
