/**
 * Class Name: ResponseManager
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
package br.com.erbium.core;

import br.com.erbium.core.base.scripts.ResponseScript;
import br.com.erbium.core.base.scripts.ResponseTrigger;
import br.com.erbium.core.interfaces.ResponseManagerOperator;
import br.com.erbium.utils.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.MediaType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
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

public class ResponseManager implements ResponseManagerOperator {

    private final int DEFAULT_SIZE = 2;

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private Endpoint parentEndpoint;

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    RotatingBuffer<ErbiumResponse> responseStore = new RotatingBuffer<>(DEFAULT_SIZE);

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    LinkedList<Integer> statusCodes = new LinkedList<>();

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private Integer maxSize;

    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final Map<String, ResponseTrigger> queuedResponseTriggers = new LinkedHashMap<>();
    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final Map<String, ResponseScript> responseScripts = new LinkedHashMap<>();

    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final Map<String, String> qrsetMap = new LinkedHashMap<>();

    public ResponseManager setResponseHistorySize(@NonNull int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        if (maxSize() != null) {
            throw new IllegalArgumentException("Cannot change the size of the response history once it has been set");
        }
        if (size != DEFAULT_SIZE) {
            responseStore = new RotatingBuffer<>(size);
            maxSize = size;
        }
        return this;
    }

    public void addResponse(ErbiumResponse response) {
        responseStore().add(response);
        runResponseTriggers();
    }

    public ErbiumResponse getLastResponse() {
        return responseStore().get(0);
    }

    public ErbiumResponse getResponse() {
        return getLastResponse();
    }

    public ErbiumResponse getPenultimateResponse() {
        return getResponse(1);
    }


    public ErbiumResponse getResponse(@NonNull Integer index) {
        if (index < 0) {
            throw new IllegalArgumentException("Currently, index must be greater than or equal to: 0");
        }
        if (index > responseStore().size() - 1) {
            throw new IllegalArgumentException("Currently, index must be lesser than or equal to: " + (responseStore().size() - 1));
        }

        return responseStore().get(index);
    }

    void addResponseScript(@NonNull String name, @NonNull Consumer<ResponseManagerOperator> consumer) {
        ResponseScript wrapper = new ResponseScript() {
            @Override
            public ResponseScript exec() {
                run();
                return this;
            }

            @Override
            public void run() {
                consumer.accept(ResponseManager.this);
            }
        };
        wrapper.attach(this);
        responseScripts().put(name, wrapper);
    }

    void addResponseScript(@NonNull String name, @NonNull Class<? extends ResponseScript> scriptClass) {
        try {
            ResponseScript script = scriptClass.getDeclaredConstructor().newInstance();
            addResponseScript(name, script);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate response script from class: " + scriptClass.getName(), e);
        }
    }

    void addResponseScript(@NonNull String name, @NonNull ResponseScript script) {
        if (responseScripts().containsValue(script)) {
            System.out.println("WARNING: Endpoint script is already queued.");
            return;
        }
        if (responseScripts().containsKey(name)) {
            throw new IllegalStateException("WARNING: A response script with name '" + name + "' already exists.");
        }
        script.attach(this);
        responseScripts().put(name, script);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResponseScript> T rsm() {
        if (!responseScripts().containsKey("default")) {
            throw new IllegalStateException("No response module found.");
        }
        return (T) responseScripts().get("default");
    }

    public void rqm(@NonNull String name, Class<? extends ResponseScript> scriptClass) {
        addResponseScript(name, scriptClass);
    }

    void removeResponseScript(@NonNull String name) {
        if (!responseScripts().containsKey(name)) {
            throw new IllegalStateException("No response script with name '" + name + "' found.");
        }
        responseScripts().remove(name);
    }

    void removeResponseScripts() {
        if (responseScripts().isEmpty()) {
            System.out.println("No response scripts to remove.");
            return;
        }
        responseScripts().clear();
    }

    void runResponseScripts() {

        for (ResponseScript script : responseScripts().values()) {
            script.run(); // Execute each queued endpoint script
        }
    }

    public <T extends ResponseScript> T getResponseScript(@NonNull String name) {
        if (!responseScripts().containsKey(name)) {
            throw new IllegalStateException("No response script with name '" + name + "' found.");
        }
        return (T) responseScripts().get(name);
    }

    public <T extends ResponseScript> T getResponseScript(@NonNull String name, Class<T> clazz) {
        if (!responseScripts().containsKey(name)) {
            throw new IllegalStateException("No response script with name '" + name + "' found.");
        }
        ResponseScript script = responseScripts().get(name);
        return clazz.cast(script);
    }

    public <T extends ResponseScript> T getResponseScript(@NonNull Class<T> clazz) {

        Map<String, T> filteredScripts = new LinkedHashMap<>();
        for (Map.Entry<String, ResponseScript> entry : responseScripts().entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                filteredScripts.put(entry.getKey(), clazz.cast(entry.getValue()));
            }
        }

        if (filteredScripts.isEmpty()) {
            throw new IllegalStateException("No response script of type '" + clazz.getSimpleName() + "' found.");
        }
        if (filteredScripts.size() > 1) {
            throw new IllegalStateException("Multiple response scripts of type '" + clazz.getSimpleName() + "' found. Please use getResponseScript(String name, Class<T> clazz) instead.");
        }

        return filteredScripts.values().iterator().next();
    }

    void queueResponseTrigger(@NonNull String name, @NonNull ResponseTrigger script) {
        if (queuedResponseTriggers().containsValue(script)) {
            System.out.println("WARNING:Json request script with name '" + name + "' is already queued.");
            return;
        }
        if (queuedResponseTriggers().containsKey(name)) {
            throw new IllegalStateException("WARNING:Json request script with name '" + name + "' is already queued.");
        }
        script.attach(this);
        queuedResponseTriggers().put(name, script);
    }

    void queueResponseTrigger(@NonNull String name, @NonNull Class<? extends ResponseTrigger> scriptClass) {
        ResponseTrigger script = null;
        try {
            script = scriptClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate response trigger from class: " + scriptClass.getName(), e);
        }
        queueResponseTrigger(name, script);
    }

    void queueResponseTrigger(@NonNull String name, @NonNull Consumer<ResponseManager> consumer) {
        ResponseTrigger wrapper = new ResponseTrigger() {
            @Override
            public void run() {
                consumer.accept(ResponseManager.this);
            }
        };
        wrapper.attach(this);
        queuedResponseTriggers().put(name, wrapper);
    }

    
    void removeResponseTrigger(@NonNull String name) {
        if (queuedResponseTriggers().containsKey(name)) {
            queuedResponseTriggers().remove(name);
        } else {
            System.out.println("WARNING: Request trigger with name '" + name + "' is not queued.");
        }
    }

    
    void runResponseTriggers() {

        // Ensure any throwables will be handled in the absense of response triggers
        if (queuedResponseTriggers().isEmpty()) {
            if (getLastResponse().throwable() != null) {
                throw new RuntimeException("ERROR: An error occurred in the request.", getLastResponse().throwable());
            }
        }

        for (ResponseTrigger script : queuedResponseTriggers().values()) {
            script.run();
        }

        for (String key : qrsetMap.keySet()) {
            rset(key, qrsetMap().get(key));
        }
    }

    public ResponseManager set(@NonNull String varName, Object value) {
        parentEndpoint().parentCollection().set(varName, value);
        return this;
    }

    public Object get(@NonNull String varName) {
        return parentEndpoint().parentCollection().get(varName);
    }

    @Override
    public Routers out() {
        return parentEndpoint().parentCollection().out();
    }

    // queues an auto set of variables based on response
    public ResponseManager qrset(@NonNull String varName, @NonNull String value) {
        qrsetMap().put(varName, value);
        return this;
    }

    // queues an auto set of variables based on response
    public ResponseManager qrset(@NonNull String... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Pairs must be an even number of arguments.");
        }
        for (int i = 0; i < pairs.length; i += 2) {
            String varName = pairs[i];
            String value = pairs[i + 1];
            qrsetMap().put(varName, value);
        }
        return this;
    }

    // unqueue variables assignment
    public ResponseManager uqrset(@NonNull String varName) {
        qrsetMap().remove(varName);
        return this;
    }

    public ResponseManager uqrset() {
        qrsetMap().clear();
        return this;
    }

    public ResponseManager rset(@NonNull String varName, @NonNull String jsonPath) {
        // Gets the last response body
        ErbiumResponse response = getLastResponse();
        String responseBody = response.body();
        if (responseBody == null) {
            System.out.println("WARNING: No response content type found. Variables were not set.");
            set(varName, null);
            return this;
        }
        MediaType contentType = response.responseBody().contentType();
        if (contentType == null) {
            System.out.println("WARNING: No response content type found. Variables were not set.");
            set(varName, null);
            return this;
        }
        if (!contentType.toString().contains("application/json")) {
            System.out.println("WARNING: Content type is not JSON. Variables were not set.");
            set(varName, null);
            return this;
        }

        Object value = null;
        try {
            JsonRequest jsonReader = new JsonRequest();
            jsonReader.setBody(responseBody);
            value = jsonReader.read(jsonPath);
        } catch (Exception e) {
            System.out.println("WARNING: No value found for JSON path: " + jsonPath + ". Variables were not set.");
            set(varName, null);
            return this;
        }

        if (value == null) {
            set(varName, null);
            return this;
        }

        // Determine the type and store it in the environment
        switch (value) {
            case String s -> set(varName, s);
            case Integer i -> set(varName, i);
            case Double v -> set(varName, v);
            case Long l -> set(varName, l);
            default ->
                // Handle other types or throw an exception if the type is not supported
                set(varName, value.toString());
        }
        return this;
    }

    public ResponseManager print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }
}
