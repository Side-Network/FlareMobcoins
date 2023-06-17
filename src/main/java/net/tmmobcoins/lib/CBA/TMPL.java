package net.tmmobcoins.lib.CBA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.tmmobcoins.lib.CBA.utils.CodeArray;
import net.tmmobcoins.lib.CBA.utils.CodeCompiler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TMPL {
    private List<String> codeList = new ArrayList<>();

    ClickType clickType;

    public void setCode(String s) {
        this.codeList.add(s);
    }

    public void setCode(List<String> codeList) {
        this.codeList = codeList;
    }

    public boolean process(Player player) {
        boolean test = true;
        if (!this.codeList.isEmpty()) {
            HashMap<Integer, CodeArray> codeCompilerOutput = (new CodeCompiler()).process(this.codeList);
            for (CodeArray s : codeCompilerOutput.values()) {
                s.provideClickType(this.clickType);
                test = (test && s.checkRequirement(player));
            }
        }
        return test;
    }

    public void provideClickType(ClickType clickType) {
        this.clickType = clickType;
    }
}
