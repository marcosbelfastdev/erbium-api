package br.com.erbium.core;

import br.com.erbium.core.enums.RequestType;
import br.com.erbium.exceptions.IdentifierNotFound;
import br.com.erbium.utils.StringUtil;
import lombok.NonNull;

import java.time.Duration;
import java.util.*;
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
 * Represents a logical grouping of {@link Endpoint} objects within a {@link Workspace}.
 * Provides functionality to manage endpoints, import/export Postman collections, and handle collection-scoped variables.
 */
/**
 * Represents a logical grouping of {@link Endpoint} objects within a {@link Workspace}.
 * Provides functionality to manage endpoints, import/export Postman collections, and handle collection-scoped variables.
 */
public class Collection extends CollectionProperties {

    /**
     * Default constructor. Initializes the collection environment.
     */
    protected Collection() {
        super();
        collectionEnvironment().setParentEndpointsCollection(this);
    }

    /**
     * Constructs a {@link Collection} with the specified name.
     *
     * @param name The name of the collection.
     */
    protected Collection(@NonNull String name) {
        super(name);
        collectionEnvironment().setParentEndpointsCollection(this);
    }

    public static class Builder {
        private String name;
        private Workspace workspace;

        public Collection.Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Collection.Builder workspace(@NonNull Workspace workspace) {
            this.workspace = workspace;
            return this;
        }

        public Collection build() {
            if (name == null) throw new NullPointerException("ICollection name is required");
            if (workspace == null) throw new NullPointerException("Workspace is required");
            Collection collection = new Collection();
            collection.name(name);
            collection.workspace(workspace);
            return collection;
        }
    }


    /**
     * Assigns a UUID to this collection.
     *
     * @param collectionUid The string representation of the UUID.
     * @return This {@link Collection} instance.
     * @throws IllegalStateException If the UUID is already set.
     */
    protected Collection setUUID(@NonNull String collectionUid) {
        if (collectionUid.isEmpty()) {
            throw new IllegalArgumentException("ICollection UID cannot be null or empty.");
        }
        if (uuid() != null) {
            throw new IllegalStateException("ICollection UUID has already been set.");
        }
        uuid(UUID.fromString(collectionUid));
        return this;
    }

    /**
     * Retrieves an endpoint by name.
     *
     * @param name The name of the endpoint.
     * @return The {@link Endpoint} instance.
     * @throws IdentifierNotFound If the endpoint does not exist.
     */
    public Endpoint getEndpoint(@NonNull String name) {
        if (!endpoints().containsKey(name)) {
            throw new IdentifierNotFound("Endpoint with name '" + name + "' not found in this endpointsCollection.");
        }
        return endpoints().get(name);
    }

    /**
     * Alias for {@link #getEndpoint(String)}.
     *
     * @param name The name of the endpoint.
     * @return The {@link Endpoint} instance.
     */
    public Endpoint e$(@NonNull String name) {
        return getEndpoint(name);
    }

    /**
     * Adds an endpoint to the collection.
     * If an endpoint with the same name exists, the name will be auto-incremented.
     *
     * @param endpoint The endpoint to add.
     * @return The added endpoint.
     */
    public Endpoint addEndpoint(@NonNull Endpoint endpoint) {
        if (endpoints().containsValue(endpoint)) {
            throw new IllegalStateException("Endpoint with name '" + endpoint.name() + "' already exists in endpointsCollection: " + this.name());
        }
        if (endpoints().containsKey(endpoint.name())) {
            String newName = getAutoName(endpoint.name());
            endpoint._setName(newName);
            System.out.println("An endpoint with name '" + endpoint.name() + "' already exists. Renamed to '" + newName + "'.");
        }
        endpoint.parentCollection(this);
        endpoints().put(endpoint.name(), endpoint);
        return endpoint;
    }

    /**
     * Adds and creates a new {@link Endpoint} with the specified name and request type.
     *
     * @param name        The name of the endpoint.
     * @param requestType The request type of the endpoint.
     * @return The added endpoint.
     */
    public Endpoint addEndpoint(@NonNull String name, RequestType requestType) {
        Endpoint endpoint = Endpoint.builder()
                .name(name)
                .collection(this)
                .eagerRequestValidation(true)
                .build();
        return addEndpoint(endpoint);
    }

    /**
     * Imports a Postman collection using its UID and API key.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     * @return This {@link Collection} instance.
     */
    public Collection importPostManCollection(@NonNull String collectionUid, @NonNull String apiKey, Duration duration) {
        CollectionJsonImporter importer = new CollectionJsonImporter(this);
        return importer.importPostManCollection(collectionUid, apiKey, duration);
    }

    /**
     * Imports a Postman collection using its UID and API key.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     * @return This {@link Collection} instance.
     */
    public Collection importPostManCollection(@NonNull String collectionUid, @NonNull String apiKey) {
        CollectionJsonImporter importer = new CollectionJsonImporter(this);
        return importer.importPostManCollection(collectionUid, apiKey, null);
    }

    /**
     * Imports an endpoint from another named collection into this one.
     *
     * @param collectionName The name of the source collection.
     * @param name           The name of the endpoint.
     * @return This {@link Collection} instance.
     */
    public Collection importEndpoint(@NonNull String collectionName, @NonNull String name) {
        Collection source = workspace().getCollection(collectionName);
        if (source == null) {
            throw new IdentifierNotFound("ICollection '" + collectionName + "' not found.");
        }
        if (source.name().equals(this.name())) {
            throw new IllegalStateException("Cannot import endpoint from the same collection.");
        }
        Endpoint endpoint = source.getEndpoint(name);
        return importEndpoint(endpoint);
    }

    /**
     * Imports multiple endpoints from another collection.
     *
     * @param collectionName The source collection name.
     * @param endpointNames  Array of endpoint names.
     * @return This {@link Collection} instance.
     */
    public Collection importEndpoints(@NonNull String collectionName, @NonNull String... endpointNames) {
        for (String endpointName : endpointNames) {
            importEndpoint(collectionName, endpointName);
        }
        return this;
    }

    /**
     * Imports a specific endpoint instance.
     *
     * @param endpoint The endpoint to import.
     * @return This {@link Collection} instance.
     */
    public Collection importEndpoint(@NonNull Endpoint endpoint) {
        Collection original = endpoint.parentCollection();
        if (original.equals(this)) {
            throw new IllegalStateException("Endpoint already belongs to this collection.");
        }
        original.endpoints().values().remove(endpoint);
        endpoint.parentCollection(this);

        String targetName = getAutoName(endpoint.name());
        endpoints().put(targetName, endpoint);
        endpoint.name(targetName);
        return this;
    }

    /**
     * Imports multiple endpoint instances.
     *
     * @param endpoints Array of endpoints to import.
     * @return This {@link Collection} instance.
     */
    public Collection importEndpoints(@NonNull Endpoint... endpoints) {
        for (Endpoint endpoint : endpoints) {
            importEndpoint(endpoint);
        }
        return this;
    }

    /**
     * Imports environment variables from another collection.
     *
     * @param collectionName The name of the source collection.
     * @return This {@link Collection} instance.
     */
    public Collection importEnvironment(@NonNull String collectionName) {
        Collection other = workspace().getCollection(collectionName);
        return importEnvironment(other);
    }

    /**
     * Sets the environment variables from another collection.
     *
     * @param other The source collection.
     * @return This {@link Collection} instance.
     */
    public Collection importEnvironment(Collection other) {
        collectionEnvironment(other.collectionEnvironment());
        return this;
    }

    /**
     * Resolves and generates a unique name for an endpoint based on an existing one.
     *
     * @param endpointName The base name to resolve.
     * @return A unique name.
     */
    protected String getAutoName(String endpointName) {
        String regex = "(?: \\(Copy\\))?(?: Copy)?(?: #\\d+)?(?: \\(Copy\\))?";
        String base = endpointName.replaceAll(regex, "");

        int max = 0;
        boolean found = false;

        for (String existing : endpoints().keySet()) {
            String existingBase = existing.replaceAll(regex, "");
            if (existingBase.equals(base)) {
                found = true;
                var matcher = java.util.regex.Pattern.compile("#(\\d+)").matcher(existing);
                if (matcher.find()) {
                    int current = Integer.parseInt(matcher.group(1));
                    if (current > max) {
                        max = current;
                    }
                }
            }
        }

        return found ? base + " #" + (max + 1) : endpointName;
    }

    /**
     * Returns all endpoints in the collection.
     *
     * @return A list of all {@link Endpoint} objects.
     */

    public LinkedList<Endpoint> getEndpoints() {
        return new LinkedList<>(endpoints().values());
    }

    /**
     * Deletes an endpoint by name.
     *
     * @param endpointName The endpoint name.
     * @return This {@link Collection} instance.
     */
    public Collection deleteEndpoint(@NonNull String endpointName) {
        if (!endpoints().containsKey(endpointName)) {
            throw new IdentifierNotFound("Endpoint '" + endpointName + "' not found.");
        }
        endpoints().remove(endpointName);
        return this;
    }

    /**
     * Deletes multiple endpoints by name.
     *
     * @param endpointNames The names to delete.
     * @return This {@link Collection} instance.
     */
    public Collection deleteEndpoints(@NonNull String... endpointNames) {
        for (String name : endpointNames) {
            deleteEndpoint(name);
        }
        return this;
    }

    /**
     * Sets the host for this collection.
     *
     * @param host The host URL.
     * @return This {@link Collection} instance.
     */
    public Collection setHost(@NonNull String host) {
        host(host);
        return this;
    }

    /**
     * Removes the host from this collection.
     *
     * @return This {@link Collection} instance.
     */
    public Collection removeHost() {
        host(null);
        return this;
    }

    /**
     * Gets the host for this collection.
     *
     * @return The host URL, or an empty string if not set.
     */
    public String getHost() {
        if (host == null)
            return "";
        return host;
    }

    /**
     * Returns the current workspace context for this collection.
     * @return the Workspace instance this collection belongs to
     */
    public Workspace workspaceContext() {
        return workspace();
    }

    /**
     * Returns this collection as its own context.
     * @return this Collection instance
     */
    public Collection collectionContext() {
        return this;
    }

    // ---- Fluent interface test scripting ----

    public Collection background(@NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection background(@NonNull String description, @NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection given(@NonNull String description, @NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection given(@NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection when(@NonNull String description, @NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection when(@NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection then(@NonNull String description, @NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    public Collection then(@NonNull Consumer<Collection> script) {
        script.accept(this); // this is safe because ICollection implements ClauseCollection
        return this;
    }

    /**
     * Sets a test environment variable scoped to this collection.
     *
     * @param varName The variable name.
     * @param value   The value to assign.
     * @return This {@link Collection} instance.
     */
    public Collection set(@NonNull String varName, Object value) {
        collectionEnvironment().set(varName, value);
        return this;
    }

    public Object get(@NonNull String varName) {
        return collectionEnvironment().get(varName);
    }

    /**
     * Prints the provided messages using StringUtil and returns this instance.
     *
     * @param messages The messages to print.
     * @return This {@link Collection} instance for chaining.
     */
    public Collection print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }

    /**
     * Returns the selector associated with this collection's workspace.
     *
     * @return The {@link Selector} instance.
     */
    public Selector selector() {
        return workspace().selector();
    }

    /**
     * Applies the given consumer to all selected endpoints in the selector.
     *
     * @param consumer The consumer to apply to each selected endpoint.
     * @return This {@link Collection} instance.
     */
    public Collection selectedEndpoints(Consumer<Endpoint> consumer) {
        selector().forEach(consumer);
        return this;
    }

    /**
     * Clears the selection of endpoints in the selector.
     *
     * @return This {@link Collection} instance.
     */
    public Collection clearSelection() {
        selector().clear();
        return this;
    }
}