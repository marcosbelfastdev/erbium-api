package br.com.erbium.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

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

public class JsonPathReader {

    private final DocumentContext context;

    public JsonPathReader(String json) {
        this.context = JsonPath.using(Configuration.builder()
                        .options(Option.SUPPRESS_EXCEPTIONS)
                        .build())
                .parse(json);
    }

    /**
     * Reads a value with automatic Java type inference.
     */
    public Object read(String path) {
        Object raw = context.read(path);
        return convertValue(raw);
    }

    /**
     * Reads a value with a default fallback if not found.
     */
    public Object readOrDefault(String path, Object defaultValue) {
        Object raw = context.read(path);
        return raw != null ? convertValue(raw) : defaultValue;
    }

    /**
     * Reads an object (JSON object) as a Map.
     */
    public Map<String, Object> readObject(String path) {
        Object raw = context.read(path);
        if (raw instanceof Map<?, ?> map) {
            return castMapWithConversion(map);
        }
        return null;
    }

    /**
     * Reads an array as a List.
     */
    public List<Object> readArray(String path) {
        Object raw = context.read(path);
        if (raw instanceof List<?> list) {
            return list.stream().map(this::convertValue).toList();
        }
        return null;
    }

    /**
     * Converts a raw JSON value into the correct Java type.
     */
    private Object convertValue(Object rawValue) {
        if (rawValue == null) return null;

        if (rawValue instanceof Boolean) return rawValue;

        if (rawValue instanceof Number number) {
            if (number instanceof Integer || number instanceof Long || number instanceof Double) {
                return number;
            }

            BigDecimal bd = new BigDecimal(number.toString());
            if (bd.scale() > 0) {
                return bd.doubleValue();
            }
            try {
                return bd.intValueExact();
            } catch (ArithmeticException e) {
                try {
                    return bd.longValueExact();
                } catch (ArithmeticException ex) {
                    return bd.doubleValue();
                }
            }
        }

        if (rawValue instanceof Map<?, ?> map) {
            return castMapWithConversion(map);
        }

        if (rawValue instanceof List<?> list) {
            return list.stream().map(this::convertValue).toList();
        }

        return rawValue.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMapWithConversion(Map<?, ?> rawMap) {
        return rawMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> convertValue(e.getValue())
                ));
    }
}