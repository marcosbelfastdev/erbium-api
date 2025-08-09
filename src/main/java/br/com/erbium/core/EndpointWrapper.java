package br.com.erbium.core;

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

public class EndpointWrapper {



    Endpoint endpoint;

    public EndpointWrapper(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    Endpoint getEndpoint() {
        return endpoint;
    }


}
