package br.com.erbium.core;

import br.com.erbium.core.base.scripts.ErbiumSubmissionScript;
import br.com.erbium.core.base.scripts.RequestScript;
import br.com.erbium.core.base.scripts.RequestTrigger;
import br.com.erbium.core.interfaces.IJsonRequest;
import br.com.erbium.core.scripts._default.submission.ErbiumDefaultSubmissionScript;
import br.com.erbium.core.enums.Method;
import br.com.erbium.core.enums.RequestType;
import br.com.erbium.utils.MapPrinter;
import br.com.erbium.utils.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

public class RequestManager {

    @Getter
    @Accessors(fluent = true)
    Endpoint parentEndpoint;

    @Getter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    JsonRequest jsonRequest;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    UrlEncoded urlEncoded;
    @Getter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    XmlRequest xmlRequest;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    CommittedRequestProperties committedRequestProperties;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private ErbiumSubmissionScript erbiumSubmissionScript;

    Map<String, String> params = new LinkedHashMap<>();

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    final Map<String, RequestTrigger> queuedRequestTriggers = new LinkedHashMap<>();
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    final Map<String, RequestScript> requestScripts = new LinkedHashMap<>();

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    boolean eagerRequestValidation = false;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected RequestType requestType;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected String method;
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    protected String host;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected String url;

    public Routers out() {
        return parentEndpoint().out();
    }

    protected void verifyMethodValidity(String method) {
        if (!Method.isValid(method.trim()))
            throw new IllegalStateException("The method '" + method + "' is not valid. Please use a valid HTTP method.");
    }

    protected void verifyMethodValidity(Method method) {
        if (method == null || !Method.isValid(method.toString()))
            throw new IllegalStateException("The method '" + method + "' is not valid. Please use a valid HTTP method.");
    }

    public String getMethod() {
        return method();
    }


    public RequestType getRequestType() {
        return requestType();
    }

    void setEndpointEngine(Endpoint parentEndpoint) {
        if (this.parentEndpoint == null)
            this.parentEndpoint = parentEndpoint;
    }

    public static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (!params.isEmpty()) {
            sb.append("?");
            params.forEach((key, value) -> {
                if (sb.charAt(sb.length() - 1) != '?')
                    sb.append("&");
                sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            });
        }
        return sb.toString();
    }

    void setParams(@NonNull String... params) {

        this.params.clear();

        if (params.length % 2 != 0)
            throw new IllegalArgumentException("Params must be provided in key-value pairs.");

        for (int i = 0; i < params.length; i += 2) {
            this.params.put(params[i], params[i + 1]);
        }
    }

    void setParams(@NonNull Map<String, String> params) {
        this.params.clear();
        this.params.putAll(params);
    }

    void addParams(@NonNull String... params) {

        if (params.length % 2 != 0)
            throw new IllegalArgumentException("Params must be provided in key-value pairs.");

        for (int i = 0; i < params.length; i += 2) {
            this.params.remove(params[i]);
            this.params.put(params[i], params[i + 1]);
        }
    }

    void addParams(@NonNull Map<String, String> params) {
        this.params.putAll(params);
    }

    void clearParams() {
        this.params.clear();
    }


    IJsonRequest getInternalJsonRequest() {
        String body = jsonRequest().body();
        if (body != null) {
            String normalizedBody = parentEndpoint().parentCollection().collectionEnvironment().replaceVars(body);
            jsonRequest().body(normalizedBody);
        }

        requestType(RequestType.JSON);
        return jsonRequest;
    }

    public IJsonRequest getJsonRequest() {
        return getInternalJsonRequest();
    }

    public UrlEncoded getUrlEncoded() {
        requestType(RequestType.URL_ENCODED);
        return urlEncoded();
    }


    XmlRequest getXmlRequest() {
        requestType(RequestType.XML);
        return xmlRequest;
    }


    void setJsonRequest(@NonNull JsonRequest jsonRequest) {
        if (this.jsonRequest != null)
            throw new IllegalStateException("Json Request Manager had been set already.");
        this.jsonRequest = jsonRequest;
    }


    void setXmlRequest(@NonNull XmlRequest xmlRequest) {
        if (this.xmlRequest != null)
            throw new IllegalStateException("Xml Request Manager had been set already.");
        this.xmlRequest = xmlRequest;
    }


    public void commit() {
        if (committedRequestProperties != null) {
            throw new IllegalStateException("Submission parameters have already been set for this request.");
        }
        committedRequestProperties = new CommittedRequestProperties(parentEndpoint());
        committedRequestProperties.commit();
    }

    Collection submit() {
        return send();
    }

    Collection send() {

        if (committedRequestProperties == null) {
            commit();
        }

        boolean printEnvironmentTable = out().getOutputConfiguration().getDestination(LogItem.ENVIRONMENT_TABLE) != TargetOutput.NONE;
        if (printEnvironmentTable) {
            // 1. Get the original map with Object values
            Map<String, Object> allVariables = parentEndpoint().parentCollection().collectionEnvironment().getAllVariables();

            // 2. Convert it to a Map<String, String> using a stream
            Map<String, String> stringVariables = allVariables.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> String.valueOf(entry.getValue()) // Safely handles nulls and other types
                    ));

            // 3. Pass the correctly typed map to the printer
            out().log(LogType.UDEF, LogItem.ENVIRONMENT_TABLE, "\n\n" + MapPrinter.getFormattedTable(stringVariables) + "\n");
        }

        // You can then use the formattedTable, for example, by printing it
        ErbiumResponse response = null;
        Throwable t = null;

        if (erbiumSubmissionScript == null) {
            ErbiumDefaultSubmissionScript script = new ErbiumDefaultSubmissionScript();
            script.attach(committedRequestProperties);
            script.run();
            response = script.response();

        } else {
            erbiumSubmissionScript().attach(committedRequestProperties);
            erbiumSubmissionScript().run();
            response = erbiumSubmissionScript().response();
        }

        // add response here *** parentEndpoint().responseManager().addResponse(response);
        parentEndpoint().responseManager().addResponse(response);
        committedRequestProperties(null);
        return parentEndpoint().parentCollection();
    }

    public ResponseManager addResponse(ErbiumResponse response) {
        parentEndpoint().responseManager().addResponse(response);
        return parentEndpoint().responseManager();
    }


    public RequestManager setMethod(@NonNull String method) {
        verifyMethodValidity(method);
        method(method.toUpperCase().trim());
        return this;
    }


    public RequestManager setMethod(@NonNull Method method) {
        verifyMethodValidity(method);

        method(method.name());
        return this;
    }

    public RequestManager setHost(@NonNull String host) {
        this.host = host;
        return this;
    }

    public RequestManager removeHost() {
        this.host = null;
        return this;
    }

    public String getHost() {
        if (host != null) {
            if (!host.isEmpty()) {
                return host;
            }
        }
        return parentEndpoint().parentCollection().getHost();
    }


    public RequestManager setUrl(@NonNull String url) {
        url(url);
        return this;
    }

    public <T> T getRequest() {
        if (requestType() == RequestType.JSON) {
            @SuppressWarnings("unchecked")
            T castedJsonRequest = (T) jsonRequest();
            return castedJsonRequest;
        } else if (requestType() == RequestType.URL_ENCODED) {
            @SuppressWarnings("unchecked")
            T castedUrlEncoded = (T) urlEncoded();
            return castedUrlEncoded;
        } else {
            throw new UnsupportedOperationException("Request type");
        }
    }

    public String getUrl() {
        String currentHost = getHost();
        if (currentHost == null || currentHost.isEmpty()) {
            return url();
        }
        // Ensure there's exactly one slash between host and url
        return currentHost.replaceAll("/$", "") + "/" + url().replaceAll("^/", "");
    }

    public boolean isValidateBodyImmediately() {
        return eagerRequestValidation;
    }


    Collection post() {
        if (method() == null) {
            setMethod(Method.POST);
        }
        if (method().equals("POST")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection get() {
        if (method() == null) {
            setMethod(Method.GET);
        }
        if (method().equals("GET")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection put() {
        if (method() == null) {
            parentEndpoint().setMethod(Method.PUT);
        }
        if (method().equals("PUT")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection options() {
        if (method() == null) {
            setMethod(Method.OPTIONS);
        }
        if (method().equals("OPTIONS")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection patch() {
        if (method() == null) {
            parentEndpoint().setMethod(Method.PATCH);
        }
        if (method().equals("PATCH")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection delete() {
        if (method() == null) {
            parentEndpoint().setMethod(Method.DELETE);
        }
        if (method().equals("DELETE")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    public Collection head() {
        if (method() == null) {
            setMethod(Method.HEAD);
        }
        if (method().equals("HEAD")) {
            send();
        } else {
            throw new UnsupportedOperationException("Endpoint is set as " + method());
        }
        return parentEndpoint().parentCollection();
    }

    void setRequestType(RequestType requestType) {

        this.requestType = requestType;

        if (jsonRequest() == null) {
            JsonRequest jsonRequest = new JsonRequest();
            setJsonRequest(jsonRequest);
            jsonRequest.parentRequestManager(this);
            jsonRequest.setBody("");
        }

        if (xmlRequest() == null) {
            XmlRequest xmlRequest = new XmlRequest();
            setXmlRequest(xmlRequest);
            xmlRequest.parentRequestManager(this);
        }

        if (urlEncoded() == null) {
            UrlEncoded urlEncoded = new UrlEncoded();
            urlEncoded(urlEncoded);
            urlEncoded.parentRequestManager(this);
        }
    }


    void addRequestScript(@NonNull String name, @NonNull Consumer<RequestManager> consumer) {
        RequestScript wrapper = new RequestScript() {
            @Override
            public Endpoint exec() {
                run();
                return requestManager().parentEndpoint();
            }

            @Override
            public void run() {
                consumer.accept(requestManager());
            }
        };
        wrapper.attach(RequestManager.this);
        requestScripts().put(name, wrapper);
    }

    void addRequestScript(@NonNull String name, @NonNull Class<? extends RequestScript> scriptClass) {
        try {
            RequestScript script = (RequestScript) scriptClass.getDeclaredConstructor().newInstance();
            addRequestScript(name, scriptClass.cast(script));
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate request script from class: " + scriptClass.getName(), e);
        }
    }

    void addRequestScript(@NonNull String name, @NonNull RequestScript script) {
        if (requestScripts().containsValue(script)) {
            System.out.println("WARNING: Request script already exists.");
            return;
        }
        if (requestScripts().containsKey(name)) {
            throw new IllegalStateException("WARNING: A request script with name '" + name + "' already exists.");
        }
        script.attach(this);
        requestScripts().put(name, script);
    }

    void removeRequestScript(@NonNull String name) {
        if (!requestScripts().containsKey(name)) {
            throw new IllegalStateException("No request script with name '" + name + "' found.");
        }
        requestScripts().remove(name);
    }

    void removeRequestScripts() {
        if (requestScripts().isEmpty()) {
            System.out.println("No request scripts to remove.");
            return;
        }
        requestScripts().clear();
    }

    void runRequestScripts() {

        for (RequestScript script : requestScripts().values()) {
            script.run(); // Execute each queued endpoint script
        }
    }

    public <T extends RequestScript> T getRequestScript(@NonNull String name, @NonNull Class<T> clazz) {
        if (!requestScripts().containsKey(name)) {
            throw new IllegalStateException("No request script with name '" + name + "' found.");
        }
        RequestScript script = requestScripts().get(name);
        return clazz.cast(script);
    }

    public <T extends RequestScript> T getRequestScript(@NonNull Class<T> clazz) {

        Map<String, T> filteredScripts = new LinkedHashMap<>();
        for (Map.Entry<String, RequestScript> entry : requestScripts().entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                filteredScripts.put(entry.getKey(), clazz.cast(entry.getValue()));
            }
        }

        if (filteredScripts.isEmpty()) {
            throw new IllegalStateException("No request script of type '" + clazz.getSimpleName() + "' found.");
        }
        if (filteredScripts.size() > 1) {
            throw new IllegalStateException("Multiple request scripts of type '" + clazz.getSimpleName() + "' found. Please use getRequestScript(String name, Class<T> clazz) instead.");
        }

        return filteredScripts.values().iterator().next();
    }

    @SuppressWarnings("unchecked")
    public <T extends RequestScript> T rqm() {
        if (!requestScripts().containsKey("default")) {
            throw new IllegalStateException("No request module found.");
        }
        return (T) requestScripts().get("default");
    }

    public void rqm(@NonNull String name, Class<? extends RequestScript> scriptClass) {
        addRequestScript(name, scriptClass);
    }

    void queueRequestTrigger(@NonNull String name, @NonNull RequestTrigger script) {
        if (queuedRequestTriggers().containsValue(script)) {
            System.out.println("WARNING:Json request script with name '" + name + "' is already queued.");
            return;
        }
        if (queuedRequestTriggers().containsKey(name)) {
            throw new IllegalStateException("WARNING:Json request script with name '" + name + "' is already queued.");
        }
        script.attach(this);
        queuedRequestTriggers().put(name, script);
    }

    void queueRequestTrigger(@NonNull String name, @NonNull Class<? extends RequestTrigger> scriptClass) {
        RequestTrigger script = null;
        try {
            script = scriptClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate RequestTrigger from class: " + scriptClass.getName(), e);
        }
        queueRequestTrigger(name, script);
    }

    void queueRequestTrigger(@NonNull String name, @NonNull Consumer<RequestManager> consumer) {
        RequestTrigger wrapper = new RequestTrigger() {
            @Override
            public void run() {
                exec();
            }

            @Override
            public Endpoint exec() {
                consumer.accept(RequestManager.this);
                return ((RequestManager) requestManager()).parentEndpoint();
            }
        };
        wrapper.attach(RequestManager.this);
        queuedRequestTriggers().put(name, wrapper);
    }

    void removeRequestTrigger(@NonNull String name) {
        if (queuedRequestTriggers().containsKey(name)) {
            queuedRequestTriggers().remove(name);
        } else {
            System.out.println("WARNING: Request trigger with name '" + name + "' is not queued.");
        }
    }

    void runRequestTriggers() {
        for (RequestTrigger script : queuedRequestTriggers().values()) {
            script.exec(); // Execute each queued endpoint script
        }
    }

    public RequestManager print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }

}
