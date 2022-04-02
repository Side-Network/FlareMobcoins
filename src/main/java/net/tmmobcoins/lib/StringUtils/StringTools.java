package net.tmmobcoins.lib.StringUtils;

public class StringTools {

    public static String[] stringConcatenate(String[] first, String[] second) {
        String[] addedString = new String[1 + second.length];
        System.arraycopy(first, 0, addedString, 0, 1);
        System.arraycopy(second, 0, addedString, 1, second.length);
        return addedString;
    }

}
