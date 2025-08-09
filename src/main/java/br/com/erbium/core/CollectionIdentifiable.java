package br.com.erbium.core;

import br.com.erbium.core.enums.Action;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
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

public abstract class CollectionIdentifiable {

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    protected String name;
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    protected UUID uuid;
    @Getter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    protected final Map<Action, Boolean> locks = new EnumMap<>(Action.class);

    public CollectionIdentifiable() {

    }

    public CollectionIdentifiable(@NonNull String name) {
        name(name);
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    protected void setUUID(UUID uuid) {
        if (this.uuid != null) {
            throw new IllegalStateException("UUID has already been set.");
        }
        if (uuid == null) throw new IllegalArgumentException("UUID cannot be null.");
        this.uuid = uuid;
    }

    public final String getName() {
        return name;
    }

    public final UUID getUUID() {
        return uuid;
    }

    // Locking API
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