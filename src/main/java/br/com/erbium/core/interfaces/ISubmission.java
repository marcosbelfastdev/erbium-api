package br.com.erbium.core.interfaces;

import br.com.erbium.core.Collection;
import br.com.erbium.core.ResponseManager;

/**
 * This interface defines the contract for submitting various types of HTTP requests.
 * It provides methods for common HTTP verbs, each returning a {@link ResponseManager}
 * to handle the response from the server.
 */
public interface ISubmission {
    /**
     * Submits a generic request. The actual HTTP method might be determined by the
     * implementation or context.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection submit();

    /**
     * Sends a request. Similar to {@link #submit()}, the specific HTTP method
     * might be context-dependent.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection send();

    /**
     * Submits an HTTP POST request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection post();

    /**
     * Submits an HTTP GET request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection get();

    /**
     * Submits an HTTP PUT request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection put();

    /**
     * Submits an HTTP OPTIONS request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection options();

    /**
     * Submits an HTTP PATCH request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection patch();

    /**
     * Submits an HTTP DELETE request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection delete();

    /**
     * Submits an HTTP HEAD request.
     *
     * @return A {@link ResponseManager} instance to handle the response.
     */
    Collection head();
}
