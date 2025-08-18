/**
 * Handles JSON request bodies, providing methods for manipulation, normalization, and integration with ERBIUM's request/response model.
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
package br.com.erbium.core;

import br.com.erbium.core.interfaces.IJsonRequest;
import br.com.erbium.core.interfaces.ISubmission;
import br.com.erbium.core.interfaces.RequestManagerJsonRequest;
import br.com.erbium.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.erbium.utils.JsonVerification.isValidJson;

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


public class JsonRequest extends JsonRequestModuleImporter implements IJsonRequest, RequestManagerJsonRequest, ISubmission {

    @Getter
    @Setter
    @Accessors(fluent = true)
    protected RequestManager parentRequestManager;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected String body; // original request body
    private boolean normalized = false;
    String todelete;
    DocumentContext requestContext;


    // Configure JsonPath. Default options are usually fine.
    private static final Configuration JSON_PATH_CONFIGURATION = Configuration.builder()
            .build();

    // ObjectMapper for pretty printing. It's thread-safe and efficient to reuse.
    private static final ObjectMapper PRETTY_PRINTER_MAPPER = new ObjectMapper();


    private String toPrettyString() {
        tryNormalization();
        if (requestContext == null && body == null) {
            return "{}"; // Return an empty object for an uninitialized body
        }
        try {
            Object jsonObject = getBodyContext().json();
            String prettyJson = PRETTY_PRINTER_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            //return parentRequestManager().parentEndpoint().parentEndpointsCollection().collectionEnvironment().replaceVars(prettyJson);
            return prettyJson;
        } catch (JsonProcessingException e) {
            // This should not happen if the internal state is valid JSON,
            // so we wrap it in a runtime exception to signal a critical failure.
            throw new RuntimeException("Failed to generate pretty JSON string from internal context", e);
        }
    }


    public DocumentContext getBodyContext() {
        return requestContext;
    }


    public String getBody() {
        return toPrettyString();
    }


    public JsonRequest setContext() {
        if (body.trim().isEmpty()) {
            // Handle empty or whitespace-only bodies by creating an empty JSON object context
            this.requestContext = JsonPath.parse("{}", JSON_PATH_CONFIGURATION);
        } else {
            String normalizedBody = normalize(body);
            if (!isValidJson(normalizedBody)) {
                throw new IllegalStateException("The provided body is not a valid JSON string: " + normalize(body));
            }
            this.requestContext = JsonPath.parse(normalizedBody, JSON_PATH_CONFIGURATION);
        }
        return this;
    }

    public JsonRequest setContext(String body) {
        this.body = body;
        if (!isValidJson(body)) {
            throw new IllegalStateException("The provided body is not a valid JSON string: " + normalize(body));
        }
        this.requestContext = JsonPath.parse(body, JSON_PATH_CONFIGURATION);
        return this;
    }

    public static String quoteJsonKeys(String input) {
        // Regex: procura por palavras seguidas de dois pontos que não estão entre aspas
        Pattern pattern = Pattern.compile("(?<=[{,\\s])([a-zA-Z0-9_]+)(?=\\s*:)");

        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            matcher.appendReplacement(result, "\"" + key + "\"");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public JsonRequest setBody(@NonNull String body) {
        this.body = body;
        return this;
    }


    public JsonRequest setJsonObject(String jsonPath, String value) {
        tryNormalization();
        try {
            Object parsedValue = JSON_PATH_CONFIGURATION.jsonProvider().parse(normalize(value));
            if (!(parsedValue instanceof Map)) {
                throw new IllegalArgumentException("Provided string is not a valid JSON object for path: " + jsonPath);
            }
            getBodyContext().set(jsonPath, parsedValue);
        } catch (com.jayway.jsonpath.InvalidJsonException e) {
            throw new IllegalArgumentException("Invalid JSON object string provided for path: " + jsonPath, e);
        }
        return this;
    }


    public JsonRequest setJsonObject(String jsonPath, @NonNull Map<String, Object> value) {
        tryNormalization();
        // This delegates to the existing `set` method, providing a more explicit and discoverable API.
        return set(jsonPath, value);
    }


    public JsonRequest setJsonArray(String jsonPath, String value) {
        tryNormalization();
        try {
            Object parsedValue = JSON_PATH_CONFIGURATION.jsonProvider().parse(normalize(value));
            if (!(parsedValue instanceof List)) {
                throw new IllegalArgumentException("Provided string is not a valid JSON array for path: " + jsonPath);
            }
            getBodyContext().set(jsonPath, parsedValue);
        } catch (com.jayway.jsonpath.InvalidJsonException e) {
            throw new IllegalArgumentException("Invalid JSON array string provided for path: " + jsonPath, e);
        }
        return this;
    }


    public JsonRequest setJsonArray(String jsonPath, @NonNull List<Object> value) {
        tryNormalization();
        return set(jsonPath, value);
    }


    public JsonRequest addToJsonArray(String jsonPath, String value) {
        tryNormalization();
        try {
            Object parsedValue = JSON_PATH_CONFIGURATION.jsonProvider().parse(normalize(value));
            getBodyContext().add(jsonPath, parsedValue);
        } catch (PathNotFoundException e) {
            throw new IllegalArgumentException("JSON array path not found: " + jsonPath, e);
        } catch (com.jayway.jsonpath.InvalidJsonException e) {
            throw new IllegalArgumentException("Invalid JSON object string provided for array addition: " + jsonPath, e);
        }
        return this;
    }


    public JsonRequest addToJsonArray(String jsonPath, @NonNull Object value) {
        tryNormalization();
        try {
            getBodyContext().add(jsonPath, value);
        } catch (PathNotFoundException e) {
            throw new IllegalArgumentException("JSON array path not found: " + jsonPath, e);
        }
        return this;
    }


    public JsonRequest deleteFromJsonArray(String jsonPath, String value) {
        tryNormalization();
        try {
            TypeRef<List<Object>> typeRef = new TypeRef<>() {
            };
            Object objectToDelete = JSON_PATH_CONFIGURATION.jsonProvider().parse(normalize(value));
            List<Object> currentArray = getBodyContext().read(jsonPath, typeRef);

            if (currentArray != null) {
                // IMPROVEMENT: Added logic to handle deep equality checks for lists as well as maps.
                if (objectToDelete instanceof Map) {
                    currentArray.removeIf(item -> item instanceof Map && deepEquals((Map<String, Object>) item, (Map<String, Object>) objectToDelete));
                } else if (objectToDelete instanceof List) {
                    currentArray.removeIf(item -> item instanceof List && deepListEquals((List<Object>) item, (List<Object>) objectToDelete));
                } else {
                    currentArray.removeIf(item -> Objects.equals(item, objectToDelete));
                }
                this.requestContext.set(jsonPath, currentArray);
            }
        } catch (PathNotFoundException e) {
            System.err.println("Warning: JSON array path not found for deletion: " + jsonPath + ". No deletion performed.");
        } catch (com.jayway.jsonpath.InvalidJsonException e) {
            throw new IllegalArgumentException("Invalid JSON value string provided for array deletion: " + jsonPath, e);
        }
        return this;
    }


    public JsonRequest deleteFromJsonArray(String jsonPath, @NonNull Object objectToDelete) {
        tryNormalization();
        try {
            TypeRef<List<Object>> typeRef = new TypeRef<>() {
            };
            List<Object> currentArray = getBodyContext().read(jsonPath, typeRef);

            if (currentArray != null) {
                // Use the existing deep equals logic to find and remove the object.
                if (objectToDelete instanceof Map) {
                    currentArray.removeIf(item -> item instanceof Map && deepEquals((Map<String, Object>) item, (Map<String, Object>) objectToDelete));
                } else if (objectToDelete instanceof List) {
                    currentArray.removeIf(item -> item instanceof List && deepListEquals((List<Object>) item, (List<Object>) objectToDelete));
                } else {
                    currentArray.removeIf(item -> Objects.equals(item, objectToDelete));
                }
                this.requestContext.set(jsonPath, currentArray);
            }
        } catch (PathNotFoundException e) {
            System.err.println("Warning: JSON array path not found for deletion: " + jsonPath + ". No deletion performed.");
        }
        return this;
    }


    public JsonRequest deleteJsonProperty(@NonNull String jsonPath) {
        tryNormalization();
        try {
            getBodyContext().delete(jsonPath);
        } catch (PathNotFoundException e) {
            System.err.println("Warning: JSON property path not found for deletion: " + jsonPath + ". No deletion performed.");
        }
        return this;
    }


    public JsonRequest setMap(@NonNull String jsonPath, @NonNull Map<String, Object> objectValue) {
        tryNormalization();
        updateRequest(jsonPath, objectValue);
        return this;
    }


    public JsonRequest setList(@NonNull String jsonPath, @NonNull List<Object> arrayValue) {
        tryNormalization();
        updateRequest(jsonPath, arrayValue);
        return this;
    }


    public JsonRequest setString(@NonNull String jsonPath, @NonNull String value) {
        tryNormalization();
        updateRequest(jsonPath, environment().replaceVars(value));
        return this;
    }


    public JsonRequest setInteger(@NonNull String jsonPath, @NonNull Integer value) {
        tryNormalization();
        updateRequest(jsonPath, value);
        return this;
    }


    public JsonRequest setLong(@NonNull String jsonPath, @NonNull Long value) {
        tryNormalization();
        updateRequest(jsonPath, value);
        return this;
    }


    public JsonRequest setDouble(@NonNull String jsonPath, @NonNull Double value) {
        tryNormalization();
        updateRequest(jsonPath, value);
        return this;
    }

    public JsonRequest setNumber(@NonNull String jsonPath, @NonNull Object value) {
        tryNormalization();
        if (value instanceof Integer) {
            // No conversion needed, it's already an Integer
        } else if (value instanceof Long) {
            long longValue = (Long) value;
            if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                value = (int) longValue;
            }
        } else if (value instanceof Double) {
            double doubleValue = (Double) value;
            if (doubleValue == Math.floor(doubleValue)) { // Check if it's an integer value
                if (doubleValue >= Integer.MIN_VALUE && doubleValue <= Integer.MAX_VALUE) {
                    value = (int) doubleValue;
                } else if (doubleValue >= Long.MIN_VALUE && doubleValue <= Long.MAX_VALUE) {
                    value = (long) doubleValue;
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported type for JSON value in setNumber(): " + value.getClass().getSimpleName());
        }
        updateRequest(jsonPath, value);
        return this;
    }

    public JsonRequest setBoolean(@NonNull String jsonPath, @NonNull Boolean value) {
        tryNormalization();
        updateRequest(jsonPath, value);
        return this;
    }


    public JsonRequest set(@NonNull String jsonPath, @NonNull Object value) {
        tryNormalization();
        updateRequest(jsonPath, value);
        return this;
    }

    public JsonRequest createProperty(@NonNull String jsonPath) {
        tryNormalization();

        // Try setting it directly
        try {
            getBodyContext().set(jsonPath, null);
            return this;
        } catch (PathNotFoundException e) {
            // Try to build the missing path manually
            createPathIfMissing(jsonPath);
            getBodyContext().set(jsonPath, null);
            return this;
        }
    }

    public JsonRequest removeIfValueIsNull(@NonNull String jsonPath) {
        tryNormalization();
        try {
            Object value = getBodyContext().read(jsonPath);
            if (value == null) {
                getBodyContext().delete(jsonPath);
            }
        } catch (PathNotFoundException e) {
            // Property not found, nothing to remove.
        }
        return this;
    }


    public JsonRequest removeAllNulls() {
        tryNormalization();
        Object root = getBodyContext().json();
        if (root instanceof Map) {
            deleteAllPropertiesIfValueIsNullRecursive((Map<String, Object>) root);
            this.requestContext = JsonPath.parse(root, JSON_PATH_CONFIGURATION);
        } else if (root instanceof List) {
            ((List<?>) root).forEach(item -> {
                if (item instanceof Map) {
                    deleteAllPropertiesIfValueIsNullRecursive((Map<String, Object>) item);
                }
            });
            this.requestContext = JsonPath.parse(root, JSON_PATH_CONFIGURATION);
        }
        return this;
    }

    private void deleteAllPropertiesIfValueIsNullRecursive(Map<String, Object> jsonMap) {
        tryNormalization();
        if (jsonMap == null)
            return;
        jsonMap.entrySet().removeIf(entry -> {
            Object value = entry.getValue();
            if (value == null) {
                return true;
            }
            if (value instanceof Map) {
                deleteAllPropertiesIfValueIsNullRecursive((Map<String, Object>) value);
            } else if (value instanceof List) {
                ((List<?>) value).forEach(item -> {
                    if (item instanceof Map) {
                        deleteAllPropertiesIfValueIsNullRecursive((Map<String, Object>) item);
                    }
                });
            }
            return false;
        });
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> getBodyAsMap() {
        tryNormalization();
        Object json = getBodyContext().json();
        return (json instanceof Map) ? (Map<String, Object>) json : null;
    }

    private void updateRequest(@NonNull String jsonPath, @NonNull Object value) {
        tryNormalization();
        if (getBodyContext() == null)
            throw new IllegalStateException("Request context has not been initialized. Please set the body first.");
        try {
            Object processedValue = (value instanceof String) ? normalize((String) value) : value;
            getBodyContext().set(jsonPath, processedValue);
        } catch (PathNotFoundException e) {
            createPathIfMissing(jsonPath);
            updateRequest(jsonPath, value);
            //throw new IllegalArgumentException("Failed to set value. The path '" + jsonPath + "' or a part of it does not exist or is not a valid structure for setting a value.", e);
        }
    }


//    @SuppressWarnings("unchecked")
//    public <T extends JsonRequestModule> JsonRequest addModules(T... modules) {
//        for (T module : modules) {
//            module.attachTo(this);
//            this.modules.put(module.getClass(), module);
//        }
//        return this;
//    }

    @SuppressWarnings("unchecked")
    public <T extends JsonRequestModule> T useModule(Class<T> moduleClass) {
        return (T) this.modules.get(moduleClass);
    }

    private String normalize(@NonNull String value) {
        if (environment() == null)
            throw new IllegalArgumentException("Environment is not set because the endpoint is not assigned to a endpointsCollection. Cannot normalize value: " + value);
        if (value == null) {
            throw new IllegalArgumentException("Body or value is null");
        }
        return environment().replaceVars(quoteJsonKeys(value));
    }

    @Override
    public Collection submit() {
        return parentRequestManager().submit();
    }

    @Override
    public Collection send() {
        return parentRequestManager().send();
    }

    @Override
    public Collection post() {
        return parentRequestManager().post();
    }

    @Override
    public Collection get() {
        return parentRequestManager().get();
    }

    @Override
    public Collection put() {
        return parentRequestManager().put();
    }

    @Override
    public Collection options() {
        return parentRequestManager().options();
    }

    @Override
    public Collection patch() {
        return parentRequestManager().patch();
    }

    @Override
    public Collection delete() {
        return parentRequestManager().delete();
    }

    @Override
    public Collection head() {
        return parentRequestManager().head();
    }

    @SuppressWarnings("unchecked")
    private void createPathIfMissing(String jsonPath) {
        String[] tokens = jsonPath.replaceAll("^\\$\\.", "").split("\\.");
        DocumentContext ctx = getBodyContext();
        Object node = ctx.json();

        Map<String, Object> current = (Map<String, Object>) node;
        StringBuilder pathBuilder = new StringBuilder("$");

        for (int i = 0; i <= tokens.length - 1; i++) {
            String token = tokens[i];
            pathBuilder.append(".").append(token);
            try {
                Object next = ctx.read(pathBuilder.toString());
                if (next instanceof Map) {
                    current = (Map<String, Object>) next;
                }
            } catch (PathNotFoundException e) {
                // Create missing map
                Map<String, Object> newNode = new java.util.LinkedHashMap<>();
                current.put(token, newNode);
                current = newNode;
            }
        }

        // Update the document context with the new tree
        this.requestContext = JsonPath.parse(node, JSON_PATH_CONFIGURATION);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getBodyAsList() {
        tryNormalization();
        Object json = getBodyContext().json();
        return (json instanceof List) ? (List<Object>) json : null;
    }


    public <T> T read(String jsonPath) {
        tryNormalization();
        try {
            return getBodyContext().read(jsonPath);
        } catch (PathNotFoundException e) {
            return null;
        }
    }


    public <T> T read(String jsonPath, TypeRef<T> typeRef) {
        tryNormalization();
        try {
            return getBodyContext().read(jsonPath, typeRef);
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    private boolean deepEquals(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2)
            return true;
        if (map1 == null || map2 == null || map1.size() != map2.size())
            return false;

        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);

            if (value1 instanceof Map && value2 instanceof Map) {
                if (!deepEquals((Map<String, Object>) value1, (Map<String, Object>) value2))
                    return false;
            } else if (value1 instanceof List && value2 instanceof List) {
                if (!deepListEquals((List<Object>) value1, (List<Object>) value2))
                    return false;
            } else if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private boolean deepListEquals(List<Object> list1, List<Object> list2) {
        if (list1 == list2)
            return true;
        if (list1 == null || list2 == null || list1.size() != list2.size())
            return false;

        for (int i = 0; i < list1.size(); i++) {
            Object item1 = list1.get(i);
            Object item2 = list2.get(i);

            if (item1 instanceof Map && item2 instanceof Map) {
                if (!deepEquals((Map<String, Object>) item1, (Map<String, Object>) item2))
                    return false;
            } else if (item1 instanceof List && item2 instanceof List) {
                if (!deepListEquals((List<Object>) item1, (List<Object>) item2))
                    return false;
            } else if (!Objects.equals(item1, item2)) {
                return false;
            }
        }
        return true;
    }

    CollectionEnvironment environment() {
        return parentRequestManager().parentEndpoint().parentCollection().collectionEnvironment();
    }

    //@Override
    public Endpoint backToEndpoint() {
        return (Endpoint) parentRequestManager().parentEndpoint();
    }

    //@Override
    public Collection backToCollection() {
        return (Collection) parentRequestManager().parentEndpoint().parentCollection();
    }

    private void tryNormalization() {
            this.body = normalize(this.body);
            setContext();
    }

    @Override
    protected <T extends JsonRequestModule> JsonRequest addModules(T... modules) {
        return null;
    }

    public JsonRequest print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }
}