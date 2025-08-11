package br.com.erbium.core;


import br.com.erbium.core.base.scripts.*;
import br.com.erbium.core.interfaces.HeadersManagerOperator;
import br.com.erbium.core.enums.Method;
import br.com.erbium.core.enums.RequestType;
import br.com.erbium.utils.StringUtil;
import br.com.erbium.core.interfaces.IJsonRequest;
import br.com.erbium.core.interfaces.ISubmission;
import br.com.erbium.core.interfaces.ResponseManagerOperator;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Consumer;

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
 * Represents an API endpoint within a collection.
 * <p>
 * This class encapsulates the configuration and behavior of a single endpoint,
 * including its HTTP method, URL, headers, request/response scripts, and more.
 * Endpoints are managed within a {@link Collection} and executed via a {@link RequestManager}.
 * <p>
 * <b>Usage example:</b>
 * <pre>
 *     Endpoint endpoint = Endpoint.builder()
 *         .name("MyEndpoint")
 *         .requestType(RequestType.JSON)
 *         .collection(myCollection)
 *         .build();
 *     endpoint.setMethod(Method.POST);
 *     endpoint.setUrl("https://api.example.com/resource");
 * </pre>
 * <p>
 * Related: {@link Collection}, {@link RequestManager}, {@link HeadersManager}
 */
/**
 * Represents an API endpoint within a collection.
 * This class encapsulates the configuration and behavior of a single endpoint,
 * including its HTTP method, URL, headers, request/response scripts, and more.
 * Endpoints are managed within a {@link Collection} and executed via a {@link RequestManager}.
 */
public class Endpoint extends EndpointProperties implements ISubmission {

    /**
     * Creates a new builder for constructing an {@link Endpoint} instance.
     * <p>
     * The builder pattern allows for fluent and safe construction of endpoints with all required properties.
     *
     * @return a new {@link Builder} instance for Endpoint construction.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the parent {@link Workspace} context for this endpoint.
     * This is useful for chaining and for accessing workspace-level configuration from an endpoint.
     * @return the parent Workspace
     */
    public Workspace workspaceContext() {
        if (parentCollection() == null) {
            throw new IllegalStateException("This endpoint is not assigned to a collection and workspace.");
        }
        if (parentCollection().workspace() == null) {
            throw new IllegalStateException("This endpoint collection is not assigned to a workspace.");
        }
        return parentCollection().workspaceContext();
    }

    /**
     * Returns the parent {@link Collection} context for this endpoint.
     * This is useful for chaining and for accessing collection-level configuration from an endpoint.
     * @return the parent Collection
     */
    public Collection collectionContext() {
        if (parentCollection() == null) {
            throw new IllegalStateException("This endpoint is not assigned to a collection and workspace.");
        }
        return parentCollection();
    }

    public static class Builder {
        private String name;
        private Collection parentCollection;
        private boolean eagerRequestValidation = false;

        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder collection(@NonNull Collection parentEndpointsCollection) {
            this.parentCollection = parentEndpointsCollection;
            return this;
        }

        public Builder eagerRequestValidation(boolean validateBodyImmediately) {
            this.eagerRequestValidation = validateBodyImmediately;
            return this;
        }

        public Endpoint build() {
            if (name == null)
                throw new NullPointerException("Endpoint name is required");
            if (parentCollection == null)
                throw new NullPointerException("ICollection is required");
            Endpoint endpoint = new Endpoint();

            if (endpoint.requestManager() == null) {
                endpoint.requestManager(new RequestManager());
            }

            endpoint.requestManager().setEndpointEngine(endpoint);

            if (endpoint.headersManager() == null) {
                endpoint.headersManager(new HeadersManager());
                endpoint.headersManager().headers().headersManager(endpoint.headersManager());
                endpoint.headersManager().parentEndpoint(endpoint);
            }

            if (endpoint.responseManager() == null) {
                endpoint.responseManager(new ResponseManager());
                endpoint.responseManager().parentEndpoint(endpoint);
            }

            endpoint.name(name);
            endpoint.setEagerRequestValidation(this.eagerRequestValidation);
            endpoint.parentCollection(parentCollection);
            endpoint.setRequestType(RequestType.UNDEFINED);
            return endpoint;
        }
    }

    /**
     * Protected constructor for Endpoint. Use {@link #builder()} for instantiation.
     */
    protected Endpoint() {
        super();
    }

    /**
     * Sets the request type for this endpoint.
     *
     * @param requestType The {@link RequestType} to set.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setRequestType(@NonNull RequestType requestType) {
        if (requestManager() == null) {
            requestManager(new RequestManager());
        }
        requestManager().setRequestType(requestType);
        return this;
    }

    public Endpoint setEagerRequestValidation(boolean eagerRequestValidation) {
        requestManager().eagerRequestValidation(eagerRequestValidation);
        return this;
    }

    /**
     * Returns the HTTP method of this endpoint.
     *
     * @return The HTTP method as a String.
     */
    public String getMethod() {
        return requestManager().getMethod();
    }

    /**
     * Returns the request type of this endpoint.
     *
     * @return The {@link RequestType}.
     */
    public RequestType getRequestType() {
        return requestManager().requestType();
    }


    public boolean isValidateBodyImmediately() {
        return false;
    }

    /**
     * Submits the request defined by this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection submit() {
        return getRequestManager().submit();
    }

    /**
     * Sends the request defined by this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection send() {
        return getRequestManager().send();
    }

    /**
     * Sends a POST request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection post() {
        return getRequestManager().post();
    }

    /**
     * Sends a GET request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection get() {
        return getRequestManager().get();
    }

    /**
     * Sends a PUT request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection put() {
        return getRequestManager().put();
    }

    /**
     * Sends an OPTIONS request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection options() {
        return getRequestManager().options();
    }

    /**
     * Sends a PATCH request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection patch() {
        return getRequestManager().patch();
    }

    /**
     * Sends a DELETE request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection delete() {
        return getRequestManager().delete();
    }

    /**
     * Sends a HEAD request for this endpoint.
     *
     * @return The {@link ResponseManager} handling the response.
     */
    public Collection head() {
        return getRequestManager().head();
    }

    /**
     * Returns the internal {@link RequestManager} for this endpoint.
     *
     * @return The {@link RequestManager}.
     */
    RequestManager getRequestManager() {
        return requestManager();
    }

    /**
     * Sets URL parameters for the request.
     *
     * @param params An array of strings representing key-value pairs (e.g., "key1", "value1", "key2", "value2").
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setParams(@NonNull String... params) {
        getRequestManager().setParams(params);
        return this;
    }

    /**
     * Sets URL parameters for the request.
     *
     * @param params A map of string key-value pairs for parameters.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setParams(@NonNull Map<String, String> params) {
        getRequestManager().setParams(params);
        return this;
    }

    /**
     * Adds URL parameters to the existing ones.
     *
     * @param params An array of strings representing key-value pairs (e.g., "key1", "value1", "key2", "value2").
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addParams(@NonNull String... params) {
        getRequestManager().addParams(params);
        return this;
    }

    /**
     * Adds URL parameters to the existing ones.
     *
     * @param params A map of string key-value pairs for parameters.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addParams(@NonNull Map<String, String> params) {
        getRequestManager().addParams(params);
        return this;
    }

    public Endpoint addParam(@NonNull String name, @NonNull Object value) {
        getRequestManager().addParams(name, String.valueOf(value));
        return this;
    }

    public Endpoint clearParams() {
        requestManager().clearParams();
        return this;
    }

    /**
     * Retrieves a variable from the parent collection's environment.
     *
     * @param varName The name of the variable to retrieve.
     * @return The value of the variable.
     */
    public Object get(@NonNull String varName) {
        return parentCollection().collectionEnvironment().get(varName);
    }

    /**
     * Sets a variable in the parent collection's environment.
     *
     * @param varName The name of the variable to set.
     * @param value   The value to set for the variable.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint set(@NonNull String varName, @NonNull Object value) {
        parentCollection().set(varName, value);
        return this;
    }

    /**
     * Returns the internal JSON request manager.
     *
     * @return The {@link JsonRequest} instance.
     */
    public IJsonRequest getInternalJsonRequest() {
        return requestManager().getInternalJsonRequest();
    }

    public <T> T getRequest() {
        return requestManager().getRequest();
    }

    /**
     * Returns the JSON request manager interface.
     *
     * @return The {@link IJsonRequest} instance.
     */
    public IJsonRequest getJsonRequest() {
        return requestManager().getInternalJsonRequest();
    }

    public UrlEncoded getUrlEncoded() {
        return requestManager().getUrlEncoded();
    }

    /**
     * Returns the XML request manager.
     *
     * @return The {@link XmlRequest} instance.
     */
    public XmlRequest getXmlRequest() {
        return requestManager().getXmlRequest();
    }

    /**
     * Sets the HTTP method for this endpoint using a string.
     * The method name will be converted to uppercase and trimmed.
     *
     * @param method The HTTP method as a string (e.g., "GET", "POST").
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setMethod(@NonNull String method) {
        requestManager().setMethod(method.toUpperCase().trim());
        return this;
    }

    /**
     * Sets the HTTP method for this endpoint using the {@link Method} enum.
     *
     * @param method The {@link Method} enum value.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setMethod(@NonNull Method method) {
        requestManager().setMethod(method.name().toUpperCase().trim());
        return this;
    }

    /**
     * Sets the host for the endpoint's URL.
     *
     * @param host The host string.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setHost(@NonNull String host) {
        requestManager().setHost(host);
        return this;
    }

    public Endpoint removeHost() {
        requestManager().removeHost();
        return this;
    }

    /**
     * Returns the host of the endpoint's URL.
     *
     * @return The host string.
     */
    public String getHost() {
        return requestManager().host();
    }

    /**
     * Sets the full URL for this endpoint.
     *
     * @param url The URL string.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setUrl(@NonNull String url) {
        requestManager().setUrl(url);
        return this;
    }

    /**
     * Adds a header to the request.
     *
     * @param name  The name of the header.
     * @param value The value of the header.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addHeader(@NonNull String name, @NonNull String value) {
        headersManager().headers().addHeader(new Header(name, value, "", ""));
        return this;
    }

    /**
     * Adds a header to the request with a specified type.
     *
     * @param name  The name of the header.
     * @param value The value of the header.
     * @param type  The type of the header.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addHeader(@NonNull String name, String value, String type) {
        headersManager().headers().addHeader(new Header(name, value, "", type));
        return this;
    }

    public Endpoint setHeader(@NonNull String name, @NonNull String value) {
        headersManager().headers().setHeader(new Header(name, value, "", ""));
        return this;
    }

    /**
     * Adds a header to the request with a specified type.
     *
     * @param name  The name of the header.
     * @param value The value of the header.
     * @param type  The type of the header.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint setHeader(@NonNull String name, String value, String type) {
        headersManager().headers().setHeader(new Header(name, value, "", type));
        return this;
    }

    /**
     * Removes a header from the request by its name.
     *
     * @param name The name of the header to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeHeader(@NonNull String name) {
        headersManager().headers().removeHeader(name);
        return this;
    }

    /**
     * Returns the {@link ResponseManager} associated with this endpoint.
     *
     * @return The {@link ResponseManager}.
     */
    public ResponseManager getResponseManager() {
        return responseManager();
    }

    /**
     * Adds a header to the request with a specified type and description.
     *
     * @param name        The name of the header.
     * @param value       The value of the header.
     * @param type        The type of the header.
     * @param description A description for the header.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addHeader(@NonNull String name, @NonNull String value, String type, String description) {
        headersManager.headers().addHeader(new Header(name, value, description, type));
        return this;
    }


    /**
     * Executes a consumer script on this endpoint.
     *
     * @param script The consumer function to execute, taking this {@link Endpoint} as an argument.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint exec(@NonNull Consumer<Endpoint> script) {
        script.accept(this);
        return this;
    }

    /**
     * Executes a consumer script on this endpoint with a description.
     * The description is for documentation purposes and does not affect execution.
     *
     * @param description A description of the script.
     * @param script      The consumer function to execute, taking this {@link Endpoint} as an argument.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint exec(@NonNull String description, @NonNull Consumer<Endpoint> script) {
        script.accept(this);
        return this;
    }


    public Endpoint removeSubmissionScript() {
        requestManager().erbiumSubmissionScript(null);
        return this;
    }


    public Endpoint setSubmissionScript(@NonNull Class<? extends ErbiumSubmissionScript> scriptClass) {
        ErbiumSubmissionScript script = null;
        try {
            script = scriptClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate the submission script from class: " + scriptClass.getName(), e);
        }
        requestManager().erbiumSubmissionScript(script);
        return this;
    }

    /**
     * Returns to the parent {@link Collection} of this endpoint.
     * Requires that the collection dependency is set.
     *
     * @return The parent {@link Collection}.
     * @throws IllegalStateException if the parent collection is not set.
     */
    public Collection backToCollection() {
        requireCollectionDependency();
        return parentCollection();
    }

    /**
     * Queues a header trigger to be executed before the request.
     *
     * @param name    The name of the trigger.
     * @param trigger The {@link HeadersTrigger} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueHeaderTrigger(@NonNull String name, @NonNull HeadersTrigger trigger) {
        headersManager().queueHeaderTrigger(name, trigger);
        return this;
    }

    /**
     * Queues a header trigger to be executed before the request, instantiated from a class.
     *
     * @param name         The name of the trigger.
     * @param triggerClass The class of the {@link HeadersTrigger} to instantiate.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueHeaderTrigger(@NonNull String name, @NonNull Class<? extends HeadersTrigger> triggerClass) {
        headersManager().queueHeaderTrigger(name, triggerClass);
        return this;
    }

    /**
     * Queues an anonymous header trigger to be executed before the request.
     *
     * @param script The {@link HeadersTrigger} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueHeaderTrigger(@NonNull HeadersTrigger script) {
        headersManager().queueHeaderTrigger(script);
        return this;
    }

    /**
     * Queues a header trigger defined by a consumer function.
     *
     * @param name     The name of the trigger.
     * @param consumer The consumer function that takes a {@link HeadersManagerOperator}.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueHeaderTrigger(@NonNull String name, @NonNull Consumer<HeadersManagerOperator> consumer) {
        headersManager().queueHeaderTrigger(name, consumer);
        return this;
    }

    /**
     * Removes a header trigger by its name.
     *
     * @param name The name of the trigger to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeHeaderTrigger(@NonNull String name) {
        headersManager().removeHeaderTrigger(name);
        return this;
    }

    /**
     * Adds a request script defined by a consumer function.
     *
     * @param name     The name of the script.
     * @param consumer The consumer function that takes a {@link RequestManager}.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addRequestScript(@NonNull String name, @NonNull Consumer<RequestManager> consumer) {
        requestManager().addRequestScript(name, consumer);
        return this;
    }

    /**
     * Adds a request script instantiated from a class.
     *
     * @param name        The name of the script.
     * @param scriptClass The class of the {@link RequestScript} to instantiate.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addRequestScript(@NonNull String name, @NonNull Class<? extends RequestScript> scriptClass) {
        requestManager().addRequestScript(name, scriptClass);
        return this;
    }

    /**
     * Adds a request script instance.
     *
     * @param name   The name of the script.
     * @param script The {@link RequestScript} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addRequestScript(@NonNull String name, @NonNull RequestScript script) {
        requestManager().addRequestScript(name, script);
        return this;
    }

    /**
     * Removes a request script by its name.
     *
     * @param name The name of the script to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeRequestScript(@NonNull String name) {
        requestManager().removeRequestScript(name);
        return this;
    }

    /**
     * Removes all request scripts.
     *
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeRequestScripts() {
        requestManager().removeRequestScripts();
        return this;
    }

    /**
     * Retrieves a request script by its name and expected class type.
     *
     * @param name  The name of the script.
     * @param clazz The expected class type of the script.
     * @param <T>   The type of the request script.
     * @return The {@link RequestScript} instance, or null if not found or type mismatch.
     */
    public <T extends RequestScript> T getRequestScript(@NonNull String name, @NonNull Class<T> clazz) {
        return requestManager().getRequestScript(name, clazz);
    }

    /**
     * Retrieves a request script by its expected class type.
     * This is useful when only one script of a given type is expected.
     *
     * @param clazz The expected class type of the script.
     * @param <T>   The type of the request script.
     * @return The {@link RequestScript} instance, or null if not found or type mismatch.
     */
    public <T extends RequestScript> T getRequestScript(@NonNull Class<T> clazz) {
        return requestManager().getRequestScript(clazz);
    }


    public <T extends RequestScript> T rqm() {
        return requestManager().rqm();
    }

    public Endpoint rqm(@NonNull String name, @NonNull Class<? extends RequestScript> scriptClass) {
        requestManager().rqm(name, scriptClass);
        return this;
    }

    /**
     * Queues a request trigger to be executed before the request.
     *
     * @param name   The name of the trigger.
     * @param script The {@link RequestTrigger} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueRequestTrigger(@NonNull String name, @NonNull RequestTrigger script) {
        requestManager().queueRequestTrigger(name, script);
        return this;
    }

    /**
     * Queues a request trigger to be executed before the request, instantiated from a class.
     *
     * @param name        The name of the trigger.
     * @param scriptClass The class of the {@link RequestTrigger} to instantiate.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueRequestTrigger(@NonNull String name, @NonNull Class<? extends RequestTrigger> scriptClass) {
        requestManager().queueRequestTrigger(name, scriptClass);
        return this;
    }

    /**
     * Queues a request trigger defined by a consumer function.
     *
     * @param name     The name of the trigger.
     * @param consumer The consumer function that takes a {@link RequestManager}.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueRequestTrigger(@NonNull String name, @NonNull Consumer<RequestManager> consumer) {
        requestManager().queueRequestTrigger(name, consumer);
        return this;
    }

    /**
     * Removes a request trigger by its name.
     *
     * @param name The name of the trigger to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeRequestTrigger(@NonNull String name) {
        requestManager().removeRequestTrigger(name);
        return this;
    }

    /**
     * Runs all queued request triggers. This method is typically called internally before the request is sent.
     */
    void runRequestTriggers() {
        requestManager().runRequestTriggers();
    }

    /**
     * Adds a response script instance.
     *
     * @param name   The name of the script.
     * @param script The {@link ResponseScript} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addResponseScript(@NonNull String name, @NonNull ResponseScript script) {
        responseManager().addResponseScript(name, script);
        return this;
    }

    /**
     * Adds a response script instantiated from a class.
     *
     * @param name        The name of the script.
     * @param scriptClass The class of the {@link ResponseScript} to instantiate.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addResponseScript(@NonNull String name, @NonNull Class<? extends ResponseScript> scriptClass) {
        responseManager().addResponseScript(name, scriptClass);
        return this;
    }

    /**
     * Adds a response script defined by a consumer function.
     *
     * @param name     The name of the script.
     * @param consumer The consumer function that takes a {@link ResponseManager}.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint addResponseScript(@NonNull String name, @NonNull Consumer<ResponseManagerOperator> consumer) {
        responseManager().addResponseScript(name, consumer);
        return this;
    }

    public <T extends ResponseScript> T rsm() {
        return responseManager().rsm();
    }

    public Endpoint rsm(@NonNull String name, @NonNull Class<? extends ResponseScript> scriptClass) {
        responseManager().addResponseScript(name, scriptClass);
        return this;
    }

    /**
     * Removes a response script by its name.
     *
     * @param name The name of the script to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeResponseScript(@NonNull String name) {
        responseManager().removeResponseScript(name);
        return this;
    }

    /**
     * Removes all response scripts.
     *
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeResponseScripts() {
        responseManager().removeResponseScripts();
        return this;
    }

    /**
     * Retrieves a response script by its name and expected class type.
     *
     * @param name  The name of the script.
     * @param clazz The expected class type of the script.
     * @param <T>   The type of the response script.
     * @return The {@link ResponseScript} instance, or null if not found or type mismatch.
     */
    public <T extends ResponseScript> T getResponseScript(@NonNull String name, @NonNull Class<T> clazz) {
        return responseManager().getResponseScript(name, clazz);
    }

    public <T extends ResponseScript> T getResponseScript(@NonNull String name) {
        return responseManager().getResponseScript(name);
    }

    /**
     * Retrieves a response script by its expected class type.
     * This is useful when only one script of a given type is expected.
     *
     * @param clazz The expected class type of the script.
     * @param <T>   The type of the response script.
     * @return The {@link ResponseScript} instance, or null if not found or type mismatch.
     */
    public <T extends ResponseScript> T getResponseScript(@NonNull Class<T> clazz) {
        return responseManager().getResponseScript(name, clazz);
    }

    /**
     * Queues a response trigger to be executed after the response is received.
     *
     * @param name   The name of the trigger.
     * @param script The {@link ResponseTrigger} instance.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueResponseTrigger(@NonNull String name, @NonNull ResponseTrigger script) {
        responseManager().queueResponseTrigger(name, script);
        return this;
    }

    /**
     * Queues a response trigger to be executed after the response is received, instantiated from a class.
     *
     * @param name        The name of the trigger.
     * @param scriptClass The class of the {@link ResponseTrigger} to instantiate.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueResponseTrigger(@NonNull String name, @NonNull Class<? extends ResponseTrigger> scriptClass) {
        responseManager().queueResponseTrigger(name, scriptClass);
        return this;
    }

    /**
     * Queues a response trigger defined by a consumer function.
     *
     * @param name     The name of the trigger.
     * @param consumer The consumer function that takes a {@link ResponseManager}.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint queueResponseTrigger(@NonNull String name, @NonNull Consumer<ResponseManager> consumer) {
        responseManager().queueResponseTrigger(name, consumer);
        return this;
    }

    /**
     * Removes a response trigger by its name.
     *
     * @param name The name of the trigger to remove.
     * @return This {@link Endpoint} instance for fluent chaining.
     */
    public Endpoint removeResponseTrigger(@NonNull String name) {
        responseManager().removeResponseTrigger(name);
        return this;
    }

    public Collection select() {
        if (parentCollection() == null) {
            throw new IllegalStateException("The selection requires the endpoint to be assigned to a collection and workspace.");
        }
        if (parentCollection().workspace() == null) {
            throw new IllegalStateException("The selection requires the collection the endpoint is assigned to to be assigned to a workspace.");
        }
        parentCollection().workspace().selector().select(this);
        return parentCollection();
    }

    public Collection unselect() {
        if (parentCollection() == null || parentCollection().workspace() == null) {
            System.out.println("WARNING: This endpoint is not in a seletion group.");
        }
        parentCollection().workspace().selector().unselect(this);
        return parentCollection();
    }

    // queues an auto set of variables based on response
    public Endpoint qrset(@NonNull String varName, @NonNull String value) {
        responseManager().qrset(varName, value);
        return this;
    }

    public Endpoint qrset(@NonNull String... pairs) {
        responseManager().qrset(pairs);
        return this;
    }

    // unqueue variables assignment
    public Endpoint uqrset(@NonNull String varName) {
        responseManager().uqrset(varName);
        return this;
    }

    // unqueue all
    public Endpoint uqrset() {
        responseManager().uqrset();
        return this;
    }

    public Endpoint print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }

    public Endpoint setSslSecurity(SslSecurity sslSecurity) {
        this.sslSecurity = sslSecurity;
        return this;
    }


}