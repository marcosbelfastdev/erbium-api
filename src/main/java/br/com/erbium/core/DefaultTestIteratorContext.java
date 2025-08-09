/**
 * Provides iteration context and workspace management for test execution in ERBIUM.
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


import java.util.HashMap;
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

/**
 * DefaultTestIteratorContext provides iteration context and workspace management for test execution.
 */
public class DefaultTestIteratorContext {
    private final int iteration;
    private final Map<String, Object> data = new HashMap<>();

    Workspace workspace;

    public DefaultTestIteratorContext(int iteration) {
        this.iteration = iteration;
    }

    /**
     * Returns the current iteration number.
     * @return the iteration number
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * Sets a value in the context data map.
     * @param key the key
     * @param value the value
     */
    public void set(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Gets a value from the context data map.
     * @param key the key
     * @return the value
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * Checks if the context contains a key.
     * @param key the key
     * @return true if present, false otherwise
     */
    public boolean has(String key) {
        return data.containsKey(key);
    }

    /**
     * Clears the context and destroys the workspace reference.
     */
    public void clear() {
        ErbiumUtils.destroy();
        workspace(null);
    }

    /**
     * Gets the current workspace.
     * @return the Workspace
     */
    public Workspace workspace() {
        return workspace;
    }

    /**
     * Sets the workspace.
     * @param workspace the Workspace to set
     * @return the Workspace
     */
    public Workspace workspace(Workspace workspace) {
        this.workspace = workspace;
        return workspace;
    }
}
