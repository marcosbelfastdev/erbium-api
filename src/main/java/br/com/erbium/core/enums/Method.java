/**
 * Class Name: Method
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
package br.com.erbium.core.enums;

public enum Method {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT;

    public static boolean isValid(String method) {
        if (method == null) return false;
        try {
            Method.valueOf(method.toUpperCase().trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
