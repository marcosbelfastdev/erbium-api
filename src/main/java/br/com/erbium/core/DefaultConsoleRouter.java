package br.com.erbium.core;

import static br.com.erbium.core.ConsoleColors.*;

public class DefaultConsoleRouter implements ReportRouter {

    int targetOutput;
    boolean useColors = true;

    public DefaultConsoleRouter() {
        setTargetOutput(TargetOutput.CONSOLE);
    }

    @Override
    public void setTargetOutput(int targetOutput) {
        this.targetOutput = targetOutput;
    }

    @Override
    public int getTargetOutput() {
        return targetOutput;
    }

    @Override
    public void route(EType level, EItem EItem, String message) {
        if (useColors) {
            message = switch (EItem) {
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

        if (!level.equals(EType.UDEF)) {
            System.out.printf("[%s][%s] %s%n", message + " ");
        } else {
            System.out.print(message + " ");
        }

        System.out.printf(ConsoleColors.RESET);
    }

    @Override
    public void route(String message) {
        route(EType.INFO, EItem.MESSAGE, message);
    }

    @Override
    public void route(EType level, String message) {
        route(level, EItem.MESSAGE, message);
    }

    @Override
    public void route(EItem item, String message) {
        route(EType.INFO, item, message);
    }

    @Override
    public String getName() {
        return "DefaultConsoleRouter";
    }
}
