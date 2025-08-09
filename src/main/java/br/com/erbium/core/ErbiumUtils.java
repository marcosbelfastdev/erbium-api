package br.com.erbium.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

public class ErbiumUtils {

    private static ThreadLocal<ErbiumUtils> instance; // true singleton instance
    private static ThreadLocal<Endpoint> lastSubmittedEndpoint = new ThreadLocal<>();

    @Getter @Setter @Accessors(fluent = true)
    private Workspace workspace;

    public synchronized static ErbiumUtils getInstance() {
        if (instance != null) {
            if (instance.get() != null) {
                return instance.get();
            }
        }
        setInstance();
        return instance.get();
    }

    public static synchronized void destroy() {
        if (instance == null) {
            return;
        }
        instance.set(null);
    }

    public static synchronized void setInstance() {
        instance = ThreadLocal.withInitial(ErbiumUtils::new);
    }

    public static Endpoint lastSubmittedEndpoint() {
        return lastSubmittedEndpoint.get();
    }






































}
