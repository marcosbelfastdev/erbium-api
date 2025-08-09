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

public class XmlRequest {

    RequestManager parentRequestManager;

    String body;

    protected void setRequestManager(@NonNull RequestManager parentRequestManager) {
        if (this.parentRequestManager != null)
            throw new IllegalStateException("Request Manager had been set already.");
        this.parentRequestManager = parentRequestManager;
    }

    protected RequestManager getRequestManager() {
        if (parentRequestManager == null)
            throw new IllegalStateException("Request Manager has not been set yet.");
        return parentRequestManager;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void parentRequestManager(RequestManager requestManager) {

    }
}
