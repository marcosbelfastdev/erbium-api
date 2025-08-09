package br.com.erbium.core;

import br.com.erbium.core.interfaces.IJsonRequest;
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

/**
 * This class represents a module that can be attached to an {@link IJsonRequest}.
 * It holds a reference to an {@link IJsonRequest} object.
 */
public class JsonRequestModule {

    @Getter @Setter
    @Accessors(fluent = true)
    private IJsonRequest jsonRequest;

    /**
     * Attaches this module to a given {@link IJsonRequest}.
     * This method sets the internal {@code jsonRequest} property to the provided instance.
     * @param jsonRequest The {@link IJsonRequest} instance to attach to.
     */
    protected void attachTo(IJsonRequest jsonRequest) {
        jsonRequest(jsonRequest);
    }
}
