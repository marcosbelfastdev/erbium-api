/**
 * Class Name: HeadersManagerOperator
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
package br.com.erbium.core.interfaces;

import br.com.erbium.core.Headers;
import lombok.NonNull;

public interface HeadersManagerOperator {


    /**
     * Saves the current state of the headers.
     * Headers can typically only be saved once, or after they have been reinstated.
     *
     * @throws IllegalStateException if headers are already saved and have not been reinstated.
     */
    void saveHeaders();

    /**
     * Reinstates the previously saved headers, restoring them to a prior state.
     * After reinstatement, the saved headers are typically cleared.
     *
     * @throws IllegalStateException if no headers are saved to be reinstated.
     */
    void reinstateHeaders();

    // --- Inferred Getters for State ---
    // These getters would typically be part of the public API if consumers need to
    // inspect the managers' current state or managed objects.
    // Assuming 'headers()' and 'savedHeaders()' from Lombok's @Accessors(fluent = true)
    // are intended as public getters.

    /**
     * Retrieves the currently active headers managed by this HeadersManager.
     * This is typically the mutable set of headers.
     *
     * @return The {@link Headers} object representing the current headers.
     */
    Headers getHeaders();
    HeadersManagerOperator setHeaders(@NonNull Headers headers);
    Headers getLiteralHeaders();
    Headers setCommittedHeaders(@NonNull Headers headers);

    // If there's a need to explicitly get the saved headers state (even if null), include it:
    // Headers savedHeaders();
}