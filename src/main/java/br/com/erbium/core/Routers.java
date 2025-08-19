package br.com.erbium.core;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Routers {
    private OutputConfig config;
    @Getter
    final List<ReportRouter> routers = new CopyOnWriteArrayList<>();

    public Routers(OutputConfig config) {
        this.config = config;
    }

    public void setOutputConfiguration(OutputConfig config) {
        this.config = config;
    }

    public OutputConfig getOutputConfiguration() {
        return config;
    }

    public void add(ReportRouter router) {
        routers.add(router);
    }

    public void remove(ReportRouter router) {
        routers.remove(router);
    }

    public void log(LogType level, LogItem LogItem, Object message) {

        int destination = config.getDestination(LogItem);
        if (level == LogType.ERROR || level == LogType.SEVERE_WARNING) {
            destination = TargetOutput.CONSOLE_REPORT;
        }
        String messageStr = String.valueOf(message);

        if (destination != TargetOutput.NONE) {
            routeToAll(level, LogItem, messageStr, destination);
        }

    }

    public void err(Object message, Throwable throwable) {
        log(LogType.ERROR, LogItem.MESSAGE, String.valueOf(message));
        throw new RuntimeException(throwable);
    }

    public void log(Object message) {
        log(LogType.UDEF, LogItem.MESSAGE, message);
    }

    public void log(LogType level, String message) {
        log(level, LogItem.MESSAGE, message);
    }

    public void log(LogItem item, String message) {
        log(LogType.UDEF, item, message);
    }

    private void routeToAll(LogType level, LogItem LogItem, String message, int destination) {
        for (ReportRouter router : routers) {

            if (TargetOutput.includes(destination, TargetOutput.CONSOLE)) {
                if (router.getTargetOutput() == TargetOutput.CONSOLE) {
                    router.route(level, LogItem, message);
                }
            }

            if (TargetOutput.includes(destination, TargetOutput.REPORT)) {
                if (router.getTargetOutput() == TargetOutput.REPORT) {
                    router.route(level, LogItem, message);
                }
            }

        }
    }
}
