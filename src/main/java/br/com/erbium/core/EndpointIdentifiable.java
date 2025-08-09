package br.com.erbium.core;

import br.com.erbium.core.enums.Action;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

public abstract class EndpointIdentifiable {

    /**
     * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
     * Description: [Brief description of what this class does]
     *
     * License: MIT
     *
     * Trademark Notice:
     * The name ERBIUM as it relates to software for testing RESTful APIs,
     * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
     * and all related consultancy services, technical support, and training offerings
     * under the ERBIUM name are protected trademarks.
     */

    @Getter
    @Setter
    @Accessors(fluent = true)
    protected String name;
    protected UUID uuid;
    private final Map<Action, Boolean> locks = new EnumMap<>(Action.class);
    public final String DEFAULT = "__DEFAULT__";

    protected EndpointIdentifiable() {

    }

    protected EndpointIdentifiable(@NonNull String name) {
        name(name);
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalStateException("Name cannot be null.");
        if (this.name != null) {
                throw new IllegalStateException("Name has already been set.");
        }
        this.name = name;
    }

    public void _setName(String name) {
        this.name = name;
    }

    protected void setUUID(@NonNull UUID uuid) {
        if (this.uuid != null) {
            throw new IllegalStateException("UUID has already been set.");
        }
        this.uuid = uuid;
    }

    public final String getName() {
        return name;
    }

    public final UUID getUUID() {
        return uuid;
    }

    public void lock(Action action) {
        locks.put(action, true);
    }

    public void unlock(Action action) {
        locks.put(action, false);
    }

    public boolean isLocked(Action action) {
        return locks.getOrDefault(action, false);
    }

    public boolean anyLocked(Action... actions) {
        for (Action action : actions) {
            if (isLocked(action)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "', uuid=" + uuid + "}";
    }
}