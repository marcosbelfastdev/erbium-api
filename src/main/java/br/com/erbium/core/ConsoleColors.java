package br.com.erbium.core;

public final class ConsoleColors {
    // Reset
    public static final String RESET = "\u001B[0m";

    // Regular Colors
    public static final String BLACK = "\u001B[30m";
    public static final String BRIGHT_SALMON = "\u001B[38;5;203m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[34m";
    public static final String LILAC = "\u001B[38;5;183m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bold
    public static final String BOLD = "\u001B[1m";
    public static final String NORMAL = "\u001B[22m";

    // Backgrounds
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";

    private ConsoleColors() {} // Prevent instantiation
}
