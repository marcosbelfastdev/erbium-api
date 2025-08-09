/**
 * Class Name: ErbiumSubmissionScript
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

import br.com.erbium.core.ErbiumResponse;
import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class ErbiumSubmissionScript extends SubmissionScript implements Runnable {

    @Getter @Accessors(fluent = true)
    protected ErbiumResponse response;


    public ErbiumResponse setResponse(ErbiumResponse response) {
        this.response = response;
        return this.response;
    }

}
