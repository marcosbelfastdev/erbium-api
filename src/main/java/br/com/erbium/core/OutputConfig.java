package br.com.erbium.core;

import java.util.EnumMap;
import java.util.Map;

public final class OutputConfig {
    private final Map<EItem, Integer> itemDestinations;

    public OutputConfig() {
        this.itemDestinations = new EnumMap<>(EItem.class);
        setAllItems(TargetOutput.CONSOLE_REPORT);
    }

    /**
     * Sets all standard items to the specified destination
     * @param destination TargetOutput value (CONSOLE, REPORT, etc.)
     */
    public OutputConfig setAllItems(int destination) {
        for (EItem EItem : EItem.values()) {
            // Skip custom items if we want to only set standard items
            if (!EItem.name().startsWith("CUSTOM_")) {
                itemDestinations.put(EItem, destination);
            }
        }
        return this;
    }

    /**
     * Sets specific EItems to the specified destination
     * @param destination TargetOutput value
     * @param EItems EItem to configure
     */
    public OutputConfig set(int destination, EItem... EItems) {
        for (EItem EItem : EItems) {
            itemDestinations.put(EItem, destination);
        }
        return this;
    }

    public int getDestination(EItem EItem) {
        return itemDestinations.getOrDefault(EItem, TargetOutput.NONE);
    }

}
