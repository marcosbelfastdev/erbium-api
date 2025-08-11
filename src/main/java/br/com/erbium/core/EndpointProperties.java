package br.com.erbium.core;

import lombok.NonNull;

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


public abstract class EndpointProperties extends EndpointDependencies {

    protected EndpointProperties() {
        super();
    }

    protected EndpointProperties(@NonNull String name) {
        super(name);
    }

    public Routers out() {
        return parentCollection().workspace().out;
    }


}
