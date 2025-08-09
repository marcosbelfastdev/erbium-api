package br.com.erbium.core.interfaces;

import br.com.erbium.core.Collection;
import br.com.erbium.core.Endpoint;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface IJsonRequest {

    /**
     * Navigates back to the parent {@link Endpoint} object.
     * This is typically used for fluent API chaining to configure other aspects of the endpoint.
     * @return The parent {@link Endpoint} instance.
     */
    Endpoint backToEndpoint();

    /**
     * Navigates back to the parent {@link Collection} object.
     * This is typically used for fluent API chaining to configure other aspects of the endpointsCollection.
     * @return The parent {@link Collection} instance.
     */
    Collection backToCollection();

    /**
     * Retrieves the DocumentContext representing the parsed JSON body.
     * This allows direct interaction with the underlying JsonPath document.
     * @return The DocumentContext for the request body.
     * @throws IllegalStateException if the request context has not been set yet.
     */
    DocumentContext getBodyContext();

    /**
     * Retrieves the current state of the JSON body as a String.
     * This string reflects the normalized state if normalization was applied.
     * @return The JSON body as a String.
     */
    String getBody();

    /**
     * Sets the entire JSON body of the request from a String.
     * The input string will be normalized before parsing and setting.
     * @param body The new JSON body string.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setBody(@NonNull String body);

    /**
     * Sets a JSON path to an object (Map<String, Object>) within the request body.
     * @param jsonPath The JsonPath expression.
     * @param objectValue The Map<String, Object> to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setMap(String jsonPath, Map<String, Object> objectValue);
    IJsonRequest setBoolean(String jsonPath, Boolean objectValue);
    IJsonRequest setNumber(String jsonPath, Object objectValue);

    /**
     * Sets a JSON path to an array (List<Object>) within the request body.
     * Note: The method name 'setBody' here seems inconsistent with other 'set' methods for paths.
     * It's retained as per the provided code.
     * @param jsonPath The JsonPath expression.
     * @param arrayValue The List<Object> to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setList(String jsonPath, List<Object> arrayValue);

    /**
     * Sets a JSON path to a String value within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The String value to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setString(String jsonPath, String value);

    /**
     * Sets a JSON path to an Integer value within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The Integer value to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setInteger(String jsonPath, Integer value);

    /**
     * Sets a JSON path to a Long value within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The Long value to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setLong(String jsonPath, Long value);

    /**
     * Sets a JSON path to a fractional number (Double) value within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The Double value to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest setDouble(String jsonPath, Double value);

    /**
     * Sets a JSON path to a generic Object value within the request body.
     * The implementation performs auto-conversion based on the object type.
     * @param jsonPath The JsonPath expression.
     * @param value The Object value to set.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest set(String jsonPath, Object value);

    /**
     * Sets a JSON path to an object parsed from a String within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The String containing the JSON object.
     * @return This IJsonRequest instance for chaining calls.
     * @throws IllegalArgumentException if the provided string is not a valid JSON object.
     */
    IJsonRequest setJsonObject(String jsonPath, String value);

    /**
     * Sets a JSON path to an array parsed from a String within the request body.
     * @param jsonPath The JsonPath expression.
     * @param value The String containing the JSON array.
     * @return This IJsonRequest instance for chaining calls.
     * @throws IllegalArgumentException if the provided string is not a valid JSON array.
     */
    IJsonRequest setJsonArray(String jsonPath, String value);

    /**
     * Adds a new object (parsed from a string) to a JSON array at a specified JSON path.
     * @param jsonPath The JsonPath expression to the array.
     * @param value The String containing the JSON object (or primitive) to add.
     * @return This IJsonRequest instance for chaining calls.
     * @throws IllegalArgumentException if the array path is not found or the value string is invalid JSON.
     */
    IJsonRequest addToJsonArray(String jsonPath, String value);

    /**
     * Deletes an object from a JSON array at a specified JSON path by matching its contents.
     * This relies on a comparison of the parsed input value with array elements.
     * @param jsonPath The JsonPath expression to the array.
     * @param value The String containing the JSON object (or primitive) to delete.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest deleteFromJsonArray(String jsonPath, String value);


    /**
     * Removes the specified property from the JSON body if its value is null.
     * @param jsonPath The JsonPath expression to the property.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest removeIfValueIsNull(String jsonPath);

    /**
     * Removes all null properties recursively from the entire JSON request body.
     * @return This IJsonRequest instance for chaining calls.
     */
    IJsonRequest removeAllNulls();

    /**
     * Retrieves the current state of the JSON request body as a Map
     * (if the root of the body is a JSON object).
     * @return The JSON request body as a Map, or null if the root is not a JSON object.
     */
    Map<String, Object> getBodyAsMap();

    /**
     * Retrieves the current state of the JSON request body as a List
     * (if the root of the body is a JSON array).
     * @return The JSON request body as a List, or null if the root is not a JSON array.
     */
    List<Object> getBodyAsList();

    /**
     * Reads a value from the JSON document using a JsonPath expression.
     * @param jsonPath The JsonPath expression.
     * @param <T> The expected type of the result.
     * @return The value found at the specified path, or null if not found.
     */
    <T> T read(String jsonPath);

    /**
     * Reads a value from the JSON document using a JsonPath expression and a TypeRef for complex types.
     * @param jsonPath The JsonPath expression.
     * @param typeRef The TypeRef for complex types (e.g., List<String>).
     * @param <T> The expected type of the result.
     * @return The value found at the specified path, or null if not found.
     */
    <T> T read(String jsonPath, TypeRef<T> typeRef);

    IJsonRequest createProperty(String s);
}