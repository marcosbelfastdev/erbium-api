/**
 * Selector manages a set of selected Endpoints and provides operations to act on them collectively.
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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
 * Selector manages a set of selected Endpoints and provides operations to act on them collectively.
 * It allows selecting, unselecting, iterating, and submitting all selected endpoints.
 */
public class Selector {

    @Getter
    @Setter
    @Accessors(fluent = true)
    private LinkedList<EndpointWrapper> endpointWrappers = new LinkedList<>();

    /**
     * Selects the given endpoint and adds it to the selection.
     * @param endpoint the endpoint to select
     * @return this Selector for chaining
     */
    public Selector select(Endpoint endpoint) {
        endpointWrappers().add(new EndpointWrapper(endpoint));
        return this;
    }

    /**
     * Unselects the given endpoint, removing it from the selection.
     * @param endpoint the endpoint to unselect
     * @return this Selector for chaining
     */
    public Selector unselect(Endpoint endpoint) {
        for (EndpointWrapper endpointWrapper : endpointWrappers) {
            Endpoint selectedEndpoint = endpointWrapper.endpoint;
            if (selectedEndpoint.equals(endpoint)) {
                endpointWrappers().remove(endpointWrapper);
                return this;
            }
        }
        System.out.println("WARNING: Endpoint " + endpoint.name() + " was not selected.");
        return this;
    }

    /**
     * Clears all selected endpoints from this selector.
     */
    public void clear() {
        endpointWrappers.clear();
    }

    /**
     * Performs the given action for each selected endpoint.
     * @param action the action to be performed for each endpoint
     * @return this Selector for chaining
     */
    public Selector forEach(Consumer<Endpoint> action) {
        for (Endpoint endpoint : getEndpoints()) {
            action.accept(endpoint);
        }
        return this;
    }

    /**
     * Returns a list of all selected endpoints.
     * @return a LinkedList of selected Endpoint objects
     */
    public LinkedList<Endpoint> getEndpoints() {
        return endpointWrappers.stream()
                .map(EndpointWrapper::getEndpoint)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Submits all selected endpoints in order.
     * @return the Workspace of the first submitted endpoint, or null if none
     */
    public Workspace submit() {
        Workspace workspace = null;
        for (Endpoint endpoint : getEndpoints()) {
            endpoint.submit();
            workspace = workspace == null ? endpoint.parentCollection().workspace() : workspace;
        }
        return workspace;
    }

    /**
     * Prints the given messages and returns this selector.
     * @param messages the messages to print
     * @return this Selector for chaining
     */
    public Selector print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }
}
