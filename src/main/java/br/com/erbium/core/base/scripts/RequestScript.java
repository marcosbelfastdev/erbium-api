/**
 * Abstract base for request scripts that can be attached to a RequestManager and executed as runnables.
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
package br.com.erbium.core.base.scripts;

import br.com.erbium.core.Endpoint;
import br.com.erbium.core.RequestManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Abstract base for request scripts that can be attached to a RequestManager and executed as runnables.
 */
public abstract class RequestScript extends Script implements Runnable {

    @Getter @Setter @Accessors(fluent = true)
    protected RequestManager requestManager;


    /**
     * Attaches this script to a RequestManager.
     *
     * @param requestManager The RequestManager to attach to.
     */
    public void attach(RequestManager requestManager) {
        requestManager(requestManager);
    }

    /**
     * Executes the request script logic and returns the parent Endpoint.
     *
     * @return The Endpoint associated with this script.
     */
    public abstract Endpoint exec();


}
