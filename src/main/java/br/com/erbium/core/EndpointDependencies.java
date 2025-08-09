/**
 * Class Name: EndpointDependencies
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
package br.com.erbium.core;

import br.com.erbium.core.base.scripts.EndpointScript;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

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

public abstract class EndpointDependencies extends EndpointIdentifiable {

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    Collection parentCollection;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    RequestManager requestManager;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    ResponseManager responseManager;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    HeadersManager headersManager;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    SslSecurity sslSecurity;

    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final Map<String, EndpointScript>  queuedEndpointScripts = new LinkedHashMap<>();


    EndpointDependencies() {
        super();
    }

    EndpointDependencies(@NonNull String name) {
        super(name);
    }

    protected void requireCollectionDependency() {
        if (parentCollection() == null)
            throw new IllegalStateException("This endpoint is not assigned to a endpointsCollection. Please assign it to a endpointsCollection before proceeding.");
    }

    /**
     * Ensures that the request manager dependency is set.
     *
     * @throws IllegalStateException if the request manager is not set.
     */
    protected void requireRequestManagerDependency() {
        if (requestManager() == null)
            throw new IllegalStateException("This endpoint requires a request type to be set.");
    }

}
