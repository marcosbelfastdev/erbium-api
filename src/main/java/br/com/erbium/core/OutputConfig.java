package br.com.erbium.core;

import java.util.EnumMap;
import java.util.Map;

public final class OutputConfig {
    private final Map<LogItem, Integer> itemDestinations;

    public OutputConfig() {
        this.itemDestinations = new EnumMap<>(LogItem.class);
        setAllItems(TargetOutput.CONSOLE_REPORT);
    }

    /**
     * Sets all standard items to the specified destination
     * @param destination TargetOutput value (CONSOLE, REPORT, etc.)
     */
    public OutputConfig setAllItems(int destination) {
        for (LogItem LogItem : LogItem.values()) {
            // Skip custom items if we want to only set standard items
            if (!LogItem.name().startsWith("CUSTOM_")) {
                itemDestinations.put(LogItem, destination);
            }
        }
        return this;
    }

    /**
     * Sets specific LogItems to the specified destination
     * @param destination TargetOutput value
     * @param LogItems LogItem to configure
     */
    public OutputConfig set(int destination, LogItem... LogItems) {
        for (LogItem LogItem : LogItems) {
            itemDestinations.put(LogItem, destination);
        }
        return this;
    }

    public int getDestination(LogItem LogItem) {
        return itemDestinations.getOrDefault(LogItem, TargetOutput.NONE);
    }

}
