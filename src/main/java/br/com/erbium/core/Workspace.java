/**
 * The Workspace class represents a container for organizing collections and managing test execution in ERBIUM.
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

import br.com.erbium.utils.StringUtil;
import lombok.NonNull;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
 * The {@code Workspace} class represents a container for organizing {@link Collection} objects.
 * It supports registering, managing, and executing test scripts using a fluent interface that follows
 * the given/when/then structure commonly found in BDD (Behavior-Driven Development) scenarios.
 *
 * <p>This class is serializable and extends {@link WorkspaceProperties}, providing enhanced functionality
 * for test execution, context management, and dynamic collection registration.</p>
 */
public class Workspace extends WorkspaceProperties implements Serializable {

    /**
     * Constructs a new {@code Workspace} instance.
     */
    public Workspace() {
    }

    /**
     * Registers a {@link Collection} with this workspace.
     * A collection must have a unique name and must not already exist in this workspace.
     *
     * @param endpointsCollection The collection to register.
     * @throws IllegalStateException if a collection with the same instance or name already exists.
     */
    protected void registerCollection(Collection endpointsCollection) {
        if (collections().containsValue(endpointsCollection)) {
            throw new IllegalStateException("This endpointsCollection already exists: " + endpointsCollection.getName());
        }
        if (collections().containsKey(endpointsCollection.getName())) {
            throw new IllegalStateException("A different endpointsCollection object exists with the same name: " + endpointsCollection.getName());
        }

        collections().put(endpointsCollection.getName(), endpointsCollection);
    }

    /**
     * Unregisters a {@link Collection} from the workspace.
     *
     * @param endpointsCollection The collection to unregister.
     * @throws IllegalStateException if the collection is not registered.
     */
    protected void unregister(@NonNull Collection endpointsCollection) {
        if (!collections().containsValue(endpointsCollection))
            throw new IllegalStateException("ICollection is not registered.");
        collections().remove(endpointsCollection.getName());
    }

    /**
     * Adds a pre-existing {@link Collection} to this workspace.
     *
     * @param endpointsCollection The collection to add.
     * @return The added {@link Collection}.
     */
    protected Collection addCollection(Collection endpointsCollection) {
        registerCollection(endpointsCollection);
        return endpointsCollection;
    }

    /**
     * Creates a new {@link Collection} with the specified name and adds it to this workspace.
     *
     * @param name The name of the new collection.
     * @return The created and added {@link Collection}.
     */
    public Collection addCollection(@NonNull String name) {
        Collection endpointsCollection = new Collection(name);
        endpointsCollection.workspace(this);
        return addCollection(endpointsCollection);
    }

    /**
     * Imports a Postman collection using its UID and API key.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     * @return This {@link Collection} instance.
     */
    public Workspace importPostManCollection(@NonNull String collectionName, @NonNull String collectionUid, @NonNull String apiKey, Duration duration) {
        Collection collection = addCollection(collectionName);
        CollectionJsonImporter importer = new CollectionJsonImporter(collection);
        importer.importPostManCollection(collectionUid, apiKey, duration);
        return this;
    }

    /**
     * Imports a Postman collection using its UID and API key.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     * @return This {@link Collection} instance.
     */
    public Workspace importPostManCollection(@NonNull String collectionName, @NonNull String collectionUid, @NonNull String apiKey) {
        Collection collection = addCollection(collectionName);
        CollectionJsonImporter importer = new CollectionJsonImporter(collection);
        importer.importPostManCollection(collectionUid, apiKey, null);
        return this;
    }

    /**
     * Deletes a {@link Collection} from the workspace by its name.
     *
     * @param name The name of the collection to delete.
     * @return This {@link Workspace} instance for fluent chaining.
     * @throws IllegalStateException if no collection with the specified name exists.
     */
    protected Workspace deleteCollection(@NonNull String name) {
        if (!collections().containsKey(name)) {
            throw new IllegalStateException("ICollection with name '" + name + "' does not exist.");
        }
        Collection endpointsCollection = collections().get(name);
        unregister(endpointsCollection);
        return this;
    }

    /**
     * Returns a list of all registered collection names in the workspace.
     *
     * @return A list of collection names.
     */
    protected List<String> getCollectionNames() {
        return new ArrayList<>(collections.keySet());
    }

    /**
     * Retrieves a {@link Collection} by name.
     *
     * @param name The name of the collection to retrieve.
     * @return The corresponding {@link Collection}.
     * @throws IllegalStateException if the collection does not exist.
     */
    public Collection getCollection(@NonNull String name) {
        if (!collections().containsKey(name)) {
            throw new IllegalStateException("ICollection with name '" + name + "' does not exist.");
        }
        return collections().get(name);
    }

    /**
     * Shortcut method to retrieve a collection using a concise alias.
     *
     * @param name The name of the collection.
     * @return The corresponding {@link Collection}.
     */
    public Collection c$(@NonNull String name) {
        return getCollection(name);
    }

    /**
     * Returns a list of all registered {@link Collection} objects in the workspace.
     *
     * @return A list of {@link Collection} instances.
     */
    protected List<Collection> getBaseCollections() {
        return collections().values().stream().toList();
    }

    /**
     * Executes a custom background setup script on the workspace.
     *
     * @param script A {@link Consumer} that receives this {@link Workspace}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace background(@NonNull Consumer<Workspace> script) {
        script.accept(this);
        return this;
    }

    /**
     * Executes a custom background setup script with a description.
     *
     * @param description A descriptive name for the background context.
     * @param script      A {@link Consumer} that receives this {@link Workspace}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace background(@NonNull String description, @NonNull Consumer<Workspace> script) {
        script.accept(this);
        return this;
    }

    /**
     * Defines a "given" clause for a specified collection.
     *
     * @param description    A description of the setup context.
     * @param collectionName The name of the collection to operate on.
     * @param script         A {@link Consumer} accepting a {@link Collection}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace given(@NonNull String description, @NonNull String collectionName, @NonNull Consumer<Collection> script) {
        c$(collectionName).given(description, script);
        return this;
    }

    /**
     * Defines an "and" clause (another "given" block) for the specified collection.
     *
     * @param description    A description of the setup context.
     * @param collectionName The name of the collection to operate on.
     * @param script         A {@link Consumer} accepting a {@link Collection}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace and(@NonNull String description, @NonNull String collectionName, @NonNull Consumer<Collection> script) {
        c$(collectionName).given(description, script);
        return this;
    }

    /**
     * Defines a "when" clause for executing an action on the specified collection.
     *
     * @param description    A description of the action context.
     * @param collectionName The name of the collection to operate on.
     * @param script         A {@link Consumer} accepting a {@link Collection}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace when(@NonNull String description, @NonNull String collectionName, @NonNull Consumer<Collection> script) {
        return given(description, collectionName, script);
    }

    /**
     * Defines a "then" clause for assertions or validations on the specified collection.
     *
     * @param description    A description of the expected result or validation.
     * @param collectionName The name of the collection to operate on.
     * @param script         A {@link Consumer} accepting a {@link Collection}.
     * @return This {@link Workspace} instance for fluent chaining.
     */
    public Workspace then(@NonNull String description, @NonNull String collectionName, @NonNull Consumer<Collection> script) {
        return given(description, collectionName, script);
    }

    public Workspace print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }
}