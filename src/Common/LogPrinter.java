package Common;

import java.util.Arrays;

public class LogPrinter {

    private static final String RESET = "\u001B[0m";

    private static final int[][] COLORS = {
            {235, 50, 50},        // Red
            {60, 230, 60},        // Green
            {0, 255, 255},      // Cyan
            {255, 128, 0},      // Orange
            {255, 255, 0},      // Yellow
            {255, 128, 128},     // Pink
            {255, 0, 255},      // Magenta
            {128, 0, 255},      // Purple
            {90, 165, 235},     // Light blue
            {80, 80, 220}      // Blue
    };

    public static String getColorString(int[] rgbColor){
        int r = rgbColor[0];
        int g = rgbColor[1];
        int b = rgbColor[2];

        return "\u001b[38;2;" + Integer.toString(r) + ";" + Integer.toString(g) + ";" + Integer.toString(b) + "m";
    }

    public static void print(int index, String text){
        System.out.println(getColorString(COLORS[index % COLORS.length]) + text + RESET);
    }

}
