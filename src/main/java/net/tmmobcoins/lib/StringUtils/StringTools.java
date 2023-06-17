package net.tmmobcoins.lib.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringTools {

    public static String[] stringConcatenate(String[] first, String[] second) {
        String[] addedString = new String[1 + second.length];
        System.arraycopy(first, 0, addedString, 0, 1);
        System.arraycopy(second, 0, addedString, 1, second.length);
        return addedString;
    }

    public static <T> List<T> createList(T item) {
        List<T> a = new ArrayList<>();
        a.add(item);
        return a;
    }
}
