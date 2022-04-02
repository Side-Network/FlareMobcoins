package net.tmmobcoins.lib.base;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import net.tmmobcoins.lib.base.color.GradientPattern;
import net.tmmobcoins.lib.base.color.Pattern;
import net.tmmobcoins.lib.base.color.SolidPattern;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColorAPI {

    /**
     * Majority of the code is taken form the open source lib ( IridiumColorAPI )
     * @see <a href="https://github.com/Iridium-Development/IridiumColorAPI">IridiumColorAPI</a>
     */

    public static final int VERSION = VersionCheckers.getVersion();

    private static final boolean SUPPORTS_RGB = VERSION >= 16;

    private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");

    private static final Map<Color, ChatColor> COLORS = ImmutableMap.<Color, ChatColor>builder()
            .put(new Color(0), ChatColor.getByChar('0'))
            .put(new Color(170), ChatColor.getByChar('1'))
            .put(new Color(43520), ChatColor.getByChar('2'))
            .put(new Color(43690), ChatColor.getByChar('3'))
            .put(new Color(11141120), ChatColor.getByChar('4'))
            .put(new Color(11141290), ChatColor.getByChar('5'))
            .put(new Color(16755200), ChatColor.getByChar('6'))
            .put(new Color(11184810), ChatColor.getByChar('7'))
            .put(new Color(5592405), ChatColor.getByChar('8'))
            .put(new Color(5592575), ChatColor.getByChar('9'))
            .put(new Color(5635925), ChatColor.getByChar('a'))
            .put(new Color(5636095), ChatColor.getByChar('b'))
            .put(new Color(16733525), ChatColor.getByChar('c'))
            .put(new Color(16733695), ChatColor.getByChar('d'))
            .put(new Color(16777045), ChatColor.getByChar('e'))
            .put(new Color(16777215), ChatColor.getByChar('f')).build();

    private static final List<Pattern> PATTERNS = Arrays.asList(new GradientPattern(), new SolidPattern());

    public static String process(String string) {
        for (Pattern pattern : PATTERNS) {
            string = pattern.process(string);
        }

        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    public static List<String> process(List<String> strings) {
        return strings.stream()
                .map(ColorAPI::process)
                .collect(Collectors.toList());
    }

    public static String color(String string, Color color) {
        return (SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color)) + string;
    }
    public static ChatColor color(Color color) {
        return (SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color));
    }

    public static String color(String string, Color start, Color end) {

        ChatColor[] colors = createGradient(start, end, withoutSpecialChar(string).length());
        return apply(string, colors);
    }
    public static ChatColor getColor(String string) {
        return SUPPORTS_RGB ? ChatColor.of(new Color(Integer.parseInt(string, 16)))
                : getClosestColor(new Color(Integer.parseInt(string, 16)));
    }

    private static String apply(String source, ChatColor[] colors) {
        StringBuilder specialColors = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        String[] characters = source.split("");
        int outIndex = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].equals("&") || characters[i].equals("§")) {
                if (i + 1 < characters.length) {
                    if (characters[i + 1].equals("r")) {
                        specialColors.setLength(0);
                    } else {
                        specialColors.append(characters[i]);
                        specialColors.append(characters[i + 1]);
                    }
                    i++;
                } else
                    stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
            } else
                stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
        }
        return stringBuilder.toString();
    }

    
    private static String withoutSpecialChar(String source) {
        String workingString = source;
        for (String color : SPECIAL_COLORS) {
            if (workingString.contains(color)) {
                workingString = workingString.replace(color, "");
            }
        }
        return workingString;
    }

    /**
     * Returns a gradient array of chat colors.
     *
     * @param start The starting color.
     * @param end   The ending color.
     * @param step  How many colors we return.
     * @author TheViperShow
     * @since 1.0.0
     */
    
    private static ChatColor[] createGradient(Color start, Color end, int step) {
        ChatColor[] colors = new ChatColor[step];
        int stepR = Math.abs(start.getRed() - end.getRed()) / (step - 1);
        int stepG = Math.abs(start.getGreen() - end.getGreen()) / (step - 1);
        int stepB = Math.abs(start.getBlue() - end.getBlue()) / (step - 1);
        int[] direction = new int[] {
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for (int i = 0; i < step; i++) {
            Color color = new Color(start.getRed() + ((stepR * i) * direction[0]), start.getGreen() + ((stepG * i) * direction[1]), start.getBlue() + ((stepB * i) * direction[2]));
            if (SUPPORTS_RGB) {
                colors[i] = ChatColor.of(color);
            } else {
                colors[i] = getClosestColor(color);
            }
        }

        return colors;
    }

    /**
     * Returns the closest legacy color from an rgb color
     *
     * @param color The color we want to transform
     * @since 1.0.0
     */
    
    private static ChatColor getClosestColor(Color color) {
        Color nearestColor = null;
        double nearestDistance = Integer.MAX_VALUE;

        for (Color constantColor : COLORS.keySet()) {
            double distance = Math.pow(color.getRed() - constantColor.getRed(), 2) + Math.pow(color.getGreen() - constantColor.getGreen(), 2) + Math.pow(color.getBlue() - constantColor.getBlue(), 2);
            if (nearestDistance > distance) {
                nearestColor = constantColor;
                nearestDistance = distance;
            }
        }
        return COLORS.get(nearestColor);
    }
}
