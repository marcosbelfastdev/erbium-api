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

    public void add(ReportRouter router) {
        routers.add(router);
    }

    public void remove(ReportRouter router) {
        routers.remove(router);
    }

    public void log(EType level, EItem EItem, Object message) {

        int destination = config.getDestination(EItem);
        if (level == EType.ERROR || level == EType.SEVERE_WARNING) {
            destination = TargetOutput.CONSOLE_REPORT;
        }
        String messageStr = String.valueOf(message);

        if (destination != TargetOutput.NONE) {
            routeToAll(level, EItem, messageStr, destination);
        }

    }

    public void err(Object message, Throwable throwable) {
        log(EType.ERROR, EItem.MESSAGE, String.valueOf(message));
        throw new RuntimeException(throwable);
    }

    public void log(Object message) {
        log(EType.UDEF, EItem.MESSAGE, message);
    }

    public void log(EType level, String message) {
        log(level, EItem.MESSAGE, message);
    }

    public void log(EItem item, String message) {
        log(EType.UDEF, item, message);
    }

    private void routeToAll(EType level, EItem EItem, String message, int destination) {
        for (ReportRouter router : routers) {

            if (TargetOutput.includes(destination, TargetOutput.CONSOLE)) {
                if (router.getTargetOutput() == TargetOutput.CONSOLE) {
                    router.route(level, EItem, message);
                    return;
                }
            }

            if (TargetOutput.includes(destination, TargetOutput.REPORT)) {
                if (router.getTargetOutput() == TargetOutput.REPORT) {
                    router.route(level, EItem, message);
                    return;
                }
            }

            System.err.println("Failed to route message.");

        }
    }
}
