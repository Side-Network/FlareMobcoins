package net.tmmobcoins.lib.CBA.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CodeCompiler {

    public HashMap<Integer, CodeArray> process(List<String> list) {
        HashMap<Integer, CodeArray> mainStruct  = new HashMap<>();
        int ifCounter = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase("if")) {
                mainStruct.put(ifCounter, new CodeArray());
                while (!Objects.equals(list.get(i + 1), "do")) {
                    i++;
                    mainStruct.get(ifCounter).addConditions(list.get(i));
                }
            } else if (list.get(i).equalsIgnoreCase("do")) {
                boolean aa = false;
                while (!Objects.equals(list.get(i + 1), "end")) {
                    i++;

                    if(list.get(i).equalsIgnoreCase("else")) aa = true;

                    if(!list.get(i).equalsIgnoreCase("else") && aa) {
                        mainStruct.get(ifCounter).addFallCommands(list.get(i));
                    } else {
                        mainStruct.get(ifCounter).addCommands(list.get(i));
                    }
                }
                ifCounter++;
            } else if (list.get(i).equalsIgnoreCase("end")) {

            } else {
                mainStruct.put(ifCounter, new CodeArray());
                mainStruct.get(ifCounter).addCommands(list.get(i));
                ifCounter++;
            }
        }

        return mainStruct;
    }

}
