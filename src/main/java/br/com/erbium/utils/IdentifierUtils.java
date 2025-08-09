package br.com.erbium.utils;

public class IdentifierUtils {

    /**
     * Normalizes a string to be used as an uppercase identifier:
     * - Trims whitespace
     * - Converts to uppercase
     * - Removes all non-alphanumeric characters (except underscore)
     * - Ensures the result starts with a letter (prepends 'X' if necessary)
     */
    public static String normalize(String input) {
        if (input == null) {
            return null;
        }

        String cleaned = input.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9_]", "");

        if (cleaned.isEmpty()) {
            return "X";
        }

        if (!Character.isLetter(cleaned.charAt(0))) {
            cleaned = "X" + cleaned;
        }

        return cleaned;
    }

    /**
     * Like normalize, but replaces whitespace with underscores before cleanup.
     */
    public static String normalizeWithUnderscores(String input) {
        if (input == null) {
            return null;
        }

        String cleaned = input.trim()
                .toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");

        if (cleaned.isEmpty()) {
            return "X";
        }

        if (!Character.isLetter(cleaned.charAt(0))) {
            cleaned = "X" + cleaned;
        }

        return cleaned;
    }
}
