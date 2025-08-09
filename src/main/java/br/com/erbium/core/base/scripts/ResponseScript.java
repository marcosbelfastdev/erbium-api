/**
 * Abstract base for response scripts that can be attached to a ResponseManager and executed as runnables.
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

import br.com.erbium.core.ResponseManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Abstract base for response scripts that can be attached to a ResponseManager and executed as runnables.
 */
public abstract class ResponseScript extends Script implements Runnable{

    @Getter @Setter @Accessors(fluent = true)
    protected ResponseManager responseManager;

    /**
     * Attaches this script to a ResponseManager.
     *
     * @param responseManager The ResponseManager to attach to.
     */
    public void attach(ResponseManager responseManager) {
        responseManager(responseManager);
    }

    /**
     * Executes the response script logic and returns itself for chaining.
     *
     * @return This ResponseScript instance.
     */
    public abstract ResponseScript exec();

}
