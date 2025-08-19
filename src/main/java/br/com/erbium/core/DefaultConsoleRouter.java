package br.com.erbium.core;

import lombok.Setter;

import static br.com.erbium.core.ConsoleColors.*;

/**
 * A default implementation of {@link ReportRouter} that routes formatted and colored
 * output to the standard console (System.out).
 * <p>
 * This router is responsible for interpreting the type and level of a report item
 * and applying appropriate ANSI color codes to make the output more readable.
 * For example, it colors HTTP methods (POST, GET, etc.) and HTTP status codes
 * (2xx, 4xx, etc.) differently.
 */
public class DefaultConsoleRouter implements ReportRouter {

    int targetOutput;
    @Setter
    boolean useColors = true;

    /**
     * Constructs a new DefaultConsoleRouter, defaulting the output target to the console.
     */
    public DefaultConsoleRouter() {
        setTargetOutput(TargetOutput.CONSOLE);
    }

    @Override
    public void commit() {

    }

    @Override
    public void setTargetOutput(int targetOutput) {
        this.targetOutput = targetOutput;
    }

    @Override
    public int getTargetOutput() {
        return targetOutput;
    }

    /**
     * The core routing method. It formats the message with colors based on the {@link LogItem}
     * and then prints it to the console.
     * <p>
     * The final output format depends on the {@link LogType}. Regular types are printed
     * with a newline, whereas {@link LogType#UDEF} is printed without a trailing newline
     * to allow for building a single line of output from multiple calls.
     *
     * @param level   The severity or type of the log entry (e.g., INFO, UDEF).
     * @param item   The specific category of the message (e.g., REQUEST_METHOD, RESPONSE_CODE).
     * @param message The raw string message to be routed and formatted.
     */
    @Override
    public void route(LogType level, LogItem item, String message) { // TODO: Rename LogItem parameter to item
        if (useColors) {
            message = switch (item) {
                case REQUEST_METHOD -> switch (message.trim()) {
                    case "POST" -> BRIGHT_YELLOW + BOLD + message;
                    case "GET", "HEAD" -> BRIGHT_GREEN + BOLD + message;
                    case "PUT" -> BLUE + BOLD + message;
                    case "PATCH" -> LILAC + BOLD + message;
                    case "DELETE" -> BRIGHT_SALMON + BOLD + message;
                    case "OPTIONS" -> PURPLE + BOLD + message;
                    default -> message;
                };
                case RESPONSE_CODE -> {
                    int code = Integer.parseInt(message.trim());
                    if (code >= 400)
                        yield BOLD + RED + message;
                    else if (code == 200)
                        yield BOLD + BRIGHT_GREEN + message;
                    else
                        yield BOLD + BRIGHT_YELLOW + message;
                }
                default -> message;
            };
        }

        if (useColors) {
            if (level == LogType.WARNING || level == LogType.SEVERE_WARNING) {
                message = BRIGHT_YELLOW + BOLD + "WARNING: " + message;
            }

            if (level == LogType.LIGHT_WARNING) {
                message = YELLOW + message;
            }

            if (level == LogType.ERROR) {
                message = BRIGHT_SALMON + BOLD + "ERROR: " + message;
            }

            if (level == LogType.SUCESS) {
                message = BRIGHT_GREEN + message;
            }
        }

        if (!level.equals(LogType.UDEF) && !item.equals(LogItem.MESSAGE_COMPLEMENT)) {
            System.out.print(message + " ");
        } else {
            System.out.print(message + " ");
        }

        System.out.print(ConsoleColors.RESET);
    }

    /**
     * A convenience method to route a simple message with default levels.
     * Defaults to {@link LogType#INFO} and {@link LogItem#MESSAGE}.
     *
     * @param message The message to route.
     */
    @Override
    public void route(String message) {
        route(LogType.INFO, LogItem.MESSAGE, message);
    }

    /**
     * A convenience method to route a message with a specific level.
     * Defaults to {@link LogItem#MESSAGE}.
     *
     * @param level   The severity or type of the log entry.
     * @param message The message to route.
     */
    @Override
    public void route(LogType level, String message) {
        route(level, LogItem.MESSAGE, message);
    }

    /**
     * A convenience method to route a message for a specific item type.
     * Defaults to {@link LogType#INFO}.
     *
     * @param item    The specific category of the message.
     * @param message The message to route.
     */
    @Override
    public void route(LogItem item, String message) {
        route(LogType.INFO, item, message);
    }

    @Override
    public String getName() {
        return "DefaultConsoleRouter";
    }
}
