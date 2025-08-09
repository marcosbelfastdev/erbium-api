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

public abstract class JsonRequestModuleImporter {

    protected final Map<Class<?>, JsonRequestModule> modules = new HashMap<>();

    protected abstract <T extends JsonRequestModule> JsonRequest addModules(T... modules);

    @SuppressWarnings("unchecked")
    protected <T extends JsonRequestModule> T use(Class<T> type) {
        return (T) modules.get(type);
    }
}
