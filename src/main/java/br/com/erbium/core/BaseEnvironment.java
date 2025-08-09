/**
 * Abstract base class for environment variable management in ERBIUM, supporting variable replacement and type-safe storage.
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

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

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
 * Abstract base class for environment variable management in ERBIUM.
 */
public abstract class BaseEnvironment {
    protected final Map<String, Object> map = new HashMap<>();

    
    /**
     * Sets a variable in the environment. Only allows String, Integer, Long, Double, Boolean, or null values.
     *
     * @param key   The variable name.
     * @param value The value to set.
     * @return This BaseEnvironment instance for chaining.
     * @throws IllegalStateException if the value type is not supported.
     */
    public BaseEnvironment set(@NonNull String key, Object value) {
        if (value == null || value instanceof String || value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Boolean) {


            key = key.replaceAll("\\{\\{", "").replaceAll("}}", "").trim();
            map.put(key, value); // Use put; if key exists, it will be replaced. No need for try-catch with replace.
        } else
            throw new IllegalStateException("Object type not supported.");
        return this;
    }

    
    /**
     * Removes a variable from the environment.
     *
     * @param key The variable name to remove.
     */
    public void remove(String key) {
        try {
            map.remove(key);
        } catch (Exception ignore) {

        }
    }

    
    /**
     * Gets the value of a variable from the environment.
     *
     * @param key The variable name.
     * @return The value, or null if not found.
     */
    public abstract Object get(String key);

    
    /**
     * Gets the value of a variable, returning an empty string if the value is null.
     *
     * @param key The variable name.
     * @return The value, or an empty string if null.
     */
    public Object getNonNull(String key) {
        Object value = get(key);
        return value == null ? "" : value;
    }

    
    /**
     * Replaces all variable placeholders in the input string with their values from the environment.
     *
     * @param input The input string containing placeholders.
     * @return The string with variables replaced.
     */
    public String replaceVars(@NonNull String input) {

        String result = input;


        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());

            result = result.replace("{{" + key + "}}", value);

        }

        return result;
    }

    
    /**
     * Replaces all variable placeholders in the input string with their values, using "null" for null values.
     *
     * @param input The input string containing placeholders.
     * @return The string with variables replaced, using "null" for nulls.
     */
    public String replaceVarsAcceptNull(@NonNull String input) {

        String result = input;


        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "null" : String.valueOf(entry.getValue());

            result = result.replace("{{" + key + "}}", value);
        }

        return result;
    }

    
    /**
     * Returns a copy of all variables in the environment.
     *
     * @param <T> The type of the values (for compatibility).
     * @return A map of all variables.
     */
    public <T> Map<String, Object> getAllVariables() {
        return new HashMap<>(map);
    }

    /**
     * Converts a value to its string representation, or null if the value is null.
     *
     * @param value The value to convert.
     * @return The string representation, or null if value is null.
     */
    public String toString(Object value) {

        if (value == null)
            return null;

        return value.toString();
    }

}
