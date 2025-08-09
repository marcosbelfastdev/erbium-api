/**
 * Abstract base for objects that can be identified by a name and UUID within a workspace context.
 *
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */
package br.com.erbium.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

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

/**
 * Abstract base for objects that can be identified by a name and UUID within a workspace context.
 */
public abstract class WorkspaceIdentifiable {

    @Getter
    @Setter
    @Accessors(fluent = true)
    protected String name;
    protected UUID uuid;

    /**
     * Default constructor for WorkspaceIdentifiable.
     */
    protected WorkspaceIdentifiable() {

    }

    /**
     * Constructs a WorkspaceIdentifiable with the given name.
     *
     * @param name The name to assign.
     */
    protected WorkspaceIdentifiable(@NonNull String name) {
        name(name);
    }

    /**
     * Sets the name for this object. Can only be set once.
     *
     * @param name The name to set.
     * @throws IllegalStateException if the name is already set or null.
     */
    public void setName(String name) {
        if (name == null)
            throw new IllegalStateException("Name cannot be null.");
        if (this.name != null) {
                throw new IllegalStateException("Name has already been set.");
        }
        this.name = name;
    }

    /**
     * Sets the UUID for this object. Can only be set once.
     *
     * @param uuid The UUID to set.
     * @throws IllegalStateException    if the UUID is already set.
     * @throws IllegalArgumentException if the UUID is null.
     */
    protected void setUUID(UUID uuid) {
        if (this.uuid != null) {
            throw new IllegalStateException("UUID has already been set.");
        }
        if (uuid == null) throw new IllegalArgumentException("UUID cannot be null.");
        this.uuid = uuid;
    }

    /**
     * Gets the name of this object.
     *
     * @return The name.
     */
    protected final String getName() {
        return name;
    }

    /**
     * Gets the UUID of this object.
     *
     * @return The UUID.
     */
    protected final UUID getUUID() {
        return uuid;
    }


    /**
     * Returns a string representation of this object, including its name and UUID.
     *
     * @return A string representation of the object.
     */
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "', uuid=" + uuid + "}";
    }
}