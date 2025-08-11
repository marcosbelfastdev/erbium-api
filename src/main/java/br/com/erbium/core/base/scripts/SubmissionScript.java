/**
 * Abstract base for submission scripts, providing a reference to committed request properties.
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

import br.com.erbium.core.CommittedRequestProperties;
import br.com.erbium.core.Routers;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Abstract base for submission scripts, providing a reference to committed request properties.
 */
public abstract class SubmissionScript extends Script {

    @Getter @Setter @Accessors(fluent = true)
    protected CommittedRequestProperties committedRequestProperties;

    @Getter @Setter @Accessors(fluent = true)
    protected Routers out;



    /**
     * Attaches this script to the given committed request properties.
     *
     * @param committedRequestProperties The committed request properties to attach.
     */
    public void attach(CommittedRequestProperties committedRequestProperties) {
        committedRequestProperties(committedRequestProperties);
        out(committedRequestProperties.endpoint().collectionContext().workspaceContext().out());
    }

}
