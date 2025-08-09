package br.com.erbium.core;

import lombok.Setter;

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

@Setter
public class CollectionEnvironment extends BaseEnvironment {

    Collection parentEndpointsCollection;

    
    @Override
    public Object get(String key) {
        key = stripBraces(key);
        if (map.containsKey(key))
            return map.get(key);
        return null;
    }

    
    public Object getLocal(String key) {
        if (key.trim().startsWith("{{")) {
            key = stripBraces(key);
        }
        return map.get(key);
    }
    
    private String stripBraces(String key) {
        return key.trim().replace("{{", "").replace("}}", "").trim();
    }

    
    public Collection backToCollection() {
        if (parentEndpointsCollection == null) {
            throw new IllegalStateException("This environment is not associated with a endpointsCollection.");
        }
        return (Collection) parentEndpointsCollection;
    }

}
