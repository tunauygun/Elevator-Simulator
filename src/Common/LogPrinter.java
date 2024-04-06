package Common;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LogPrinter.java
 * <p>
 * A utility class for printing colored text to the console.
 *
 * @version 1.0, March 17, 2024
 */
public class LogPrinter {

    private static final String RESET = "\u001B[0m";

    // An array of RGB color values for different colors
    private static final int[][] COLORS = {
            {60, 230, 60},      // Green
            {0, 255, 255},      // Cyan
            {255, 150, 50},     // Orange
            {255, 255, 0},      // Yellow
            {255, 100, 200},    // Pink
            {255, 0, 255},      // Magenta
            {128, 0, 255},      // Purple
            {90, 165, 235},     // Light blue
            {80, 80, 220}       // Blue
    };

    /**
     * Generates an ANSI escape sequence for the specified RGB color.
     *
     * @param rgbColor An array containing the red, green, and blue components of the color.
     * @return The ANSI escape sequence for the specified color.
     */
    public static String getColorString(int[] rgbColor) {
        int r = rgbColor[0];
        int g = rgbColor[1];
        int b = rgbColor[2];

        return "\u001b[38;2;" + Integer.toString(r) + ";" + Integer.toString(g) + ";" + Integer.toString(b) + "m";
    }

    /**
     * Prints colored text to the console.
     *
     * @param index The index of the color in the COLORS array.
     * @param text  The text to be printed.
     */
    public static void print(int index, String text) {
        System.out.println(getColorString(COLORS[index % COLORS.length]) + text + RESET);
    }

    /**
     * Prints error message to the console.
     *
     * @param text The error message.
     */
    public static void printError(String text) {
        System.out.println("\u001B[30m" + "\u001B[101m" + text + RESET);
    }

    /**
     * Prints warning message to the console.
     *
     * @param text The warning message.
     */
    public static void printWarning(String text) {
        System.out.println("\u001B[43m" + "\u001B[30m" + text + RESET);
    }

    /**
     * Gets the string representing the current time as a timestamp
     *
     * @return Timestamp string
     */
    public static String getTimestamp() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return "(" + currentTime.format(formatter) + ")";
    }

    public static int[][] getColors() {return COLORS;}

}
