package br.com.erbium.utils;

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

public class MapPrinter {

    private static final int VALUE_COLUMN_WIDTH = 120;
    private static final int KEY_COLUMN_WIDTH = 15;

    public static String getFormattedTable(Map<String, String> map) {
        StringBuilder tableBuilder = new StringBuilder();

        // Append the top border
        appendBorder(tableBuilder);

        // Append each row
        int index = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            appendRow(index, entry.getKey(), String.valueOf(entry.getValue()), tableBuilder);
            index++;
        }

        // Append the bottom border
        appendBorder(tableBuilder);

        return tableBuilder.toString();
    }

    private static void appendBorder(StringBuilder tableBuilder) {
        tableBuilder.append("+------+").append("-".repeat(KEY_COLUMN_WIDTH)).append("--+").append("-".repeat(VALUE_COLUMN_WIDTH+2)).append("+\n");
    }

    private static void appendRow(int index, String key, String value, StringBuilder tableBuilder) {
        String[] valueLines = wrapText(value, VALUE_COLUMN_WIDTH -1);

        for (int i = 0; i < valueLines.length; i++) {
            if (i == 0) {
                tableBuilder.append(String.format("| %4d | %-15s | %-120s |\n", index, key, valueLines[i]));
            } else {
                tableBuilder.append(String.format("|      | %-15s | %-120s |\n", "", valueLines[i]));
            }
        }
    }

    private static String[] wrapText(String text, int width) {
        int length = text.length();
        int numOfChunks = (length + width - 1) / width;
        String[] chunks = new String[numOfChunks];

        for (int i = 0; i < numOfChunks; i++) {
            int start = i * width;
            int end = Math.min(start + width, length);
            chunks[i] = text.substring(start, end);
        }

        return chunks;
    }
}

