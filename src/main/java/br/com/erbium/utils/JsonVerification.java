package br.com.erbium.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.Option;

import java.util.EnumSet;

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

/**
 * A utility class to safely handle Postman-style variables (e.g., {{variable}}) within a JSON string.
 * <p>
 * Postman allows variables to be used as raw values (e.g., `{"key": {{port}}}`), which is not valid JSON.
 * This class provides `normalize` and `denormalize` methods to convert such a string into a parsable
 * format and then restore it.
 */
public class JsonVerification {

    private static final Configuration JSON_PATH_VALIDATION_CONFIGURATION = Configuration.builder()
            .options(EnumSet.noneOf(Option.class))
            .build();

    /**
     * Checks if a given string is a valid JSON.
     * It attempts to parse the string using Jayway JsonPath's underlying JSON provider.
     *
     * @param jsonString The string to validate.
     * @return true if the string is a valid JSON, false otherwise.
     */
    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        try {
            JSON_PATH_VALIDATION_CONFIGURATION.jsonProvider().parse(jsonString);
            return true;
        } catch (InvalidJsonException e) {
            return false;
        } catch (Exception e) {
            // Catch any other potential runtime exceptions during parsing
            return false;
        }
    }
}