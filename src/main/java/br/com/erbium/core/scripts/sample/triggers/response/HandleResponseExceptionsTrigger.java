/**
 * Class Name: HandleResponseExceptionsTrigger
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
package br.com.erbium.core.scripts.sample.triggers.response;

import br.com.erbium.core.base.scripts.ResponseTrigger;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

public class HandleResponseExceptionsTrigger extends ResponseTrigger {

    @Override
    public void run() {
        Throwable throwable = responseManager().getResponse().throwable();
        if (throwable != null) {
            try {
                throw throwable;
            } catch (Throwable e) {
                System.out.println("There was an error in the response.");
                throw new RuntimeException(e);
            }
        }
    }
}
