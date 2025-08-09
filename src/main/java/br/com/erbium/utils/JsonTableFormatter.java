package br.com.erbium.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
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

/**
 * Utility class for formatting JSON strings into a human-readable table format.
 * It flattens the JSON structure and presents each key-value pair or array element
 * as a row in a table, handling long values and paths by splitting them into multiple lines.
 */
public class JsonTableFormatter {
    private static final int MAX_PATH_LENGTH = 35;
    private static final int MAX_VALUE_LENGTH = 30;

    /**
     * Formats a given JSON string into a table representation.
     * Each field or array element in the JSON is represented as a row in the table.
     * Long paths or values are split into multiple lines to fit within predefined column widths.
     *
     * @param jsonString The JSON string to be formatted.
     * @return A string representing the formatted JSON in a table structure.
     * @throws Exception If the provided string is not a valid JSON.
     */
    public static String getFormattedJsonTable(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // Build the formatted table
        StringBuilder tableBuilder = new StringBuilder();
        // Initialize the row index. Using an array to allow modification within recursive calls.
        int[] index = {1};

        // Append the JSON rows
        appendJsonNode("", rootNode, index, tableBuilder);

        return tableBuilder.toString();
    }

    /**
     * Recursively appends JSON node data to the table builder.
     * It traverses the JSON tree, identifying objects, arrays, and primitive values.
     *
     * @param path The current JSON path (e.g., "data.items[0].name").
     * @param node The current JsonNode being processed.
     * @param index An array holding the current row index, incremented for each new row.
     * @param tableBuilder The StringBuilder to which formatted rows are appended.
     */
    private static void appendJsonNode(String path, JsonNode node, int[] index, StringBuilder tableBuilder) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String newPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
                appendJsonNode(newPath, field.getValue(), index, tableBuilder);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String newPath = path + "[" + i + "]";
                appendJsonNode(newPath, node.get(i), index, tableBuilder);
            }
        } else {
            // For primitive values, append a row to the table.
            // node.toString() handles various types (String, Number, Boolean, Null)
            // and wraps strings in quotes, which is desirable for JSON representation.
            appendRow(path, node.toString(), index, tableBuilder);
        }
    }

    /**
     * Appends a single row (or multiple lines for a single logical row if content is long)
     * to the table builder. It handles splitting long path and JSON value strings
     * into chunks to fit within the defined column widths.
     *
     * @param path The full path to the JSON element.
     * @param json The string representation of the JSON element's value.
     * @param index An array holding the current row index, which is incremented for each physical line appended.
     * @param tableBuilder The StringBuilder to which the formatted row(s) are appended.
     */
    private static void appendRow(String path, String json, int[] index, StringBuilder tableBuilder) {
        String[] pathChunks = splitIntoChunks(path, MAX_PATH_LENGTH);
        String[] jsonChunks = splitIntoChunks(json, MAX_VALUE_LENGTH);

        int rows = Math.max(pathChunks.length, jsonChunks.length);
        for (int i = 0; i < rows; i++) {
            // Get the current chunk for path and JSON value, or an empty string if no more chunks.
            String pathPart = i < pathChunks.length ? pathChunks[i] : "";
            String jsonPart = i < jsonChunks.length ? jsonChunks[i] : "";
            // Format the row: index, path part, JSON part.
            // %4d for index (right-aligned, 4 chars wide)
            // %-35s for path part (left-aligned, 35 chars wide)
            // %-30s for JSON part (left-aligned, 30 chars wide)
            tableBuilder.append(String.format("| %4d | %-35s | %-30s |\n", index[0]++, pathPart, jsonPart));
        }
    }

    /**
     * Splits a given string into an array of smaller strings (chunks) of a specified maximum size.
     *
     * @param str The string to be split.
     * @param chunkSize The maximum length of each chunk.
     * @return An array of strings, where each string is a chunk of the original string.
     */
    private static String[] splitIntoChunks(String str, int chunkSize) {
        int numOfChunks = (str.length() + chunkSize - 1) / chunkSize;
        String[] chunks = new String[numOfChunks];
        for (int i = 0; i < numOfChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, str.length());
            chunks[i] = str.substring(start, end);
        }
        return chunks;
    }
}
