/**
 * Class Name: HeadersTrigger
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
package br.com.erbium.core.base.scripts;

import br.com.erbium.core.Endpoint;
import br.com.erbium.core.interfaces.HeadersManagerOperator;
import br.com.erbium.core.enums.RequestType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class HeadersTrigger extends Script implements Runnable {

    @Getter @Setter @Accessors(fluent = true)
    protected HeadersManagerOperator headersManagerOperator;

    @Getter @Setter @Accessors(fluent = true)
    protected String method;

    @Getter @Setter @Accessors(fluent = true)
    protected RequestType requestType;


    public void attach(HeadersManagerOperator headersManagerOperator, Endpoint endpoint) {
        headersManagerOperator(headersManagerOperator);
        method(endpoint.getMethod());
        requestType(endpoint.getRequestType());
    }

    @Override
    public void run() {

    }

    /**
     * Implement to provide required headers for the request.
     */
    public abstract void getBasicRequirementHeaders();

    /**
     * Implement to provide cleaned-up headers for the request.
     */
    public abstract void getCleanedupHeaders();

}
