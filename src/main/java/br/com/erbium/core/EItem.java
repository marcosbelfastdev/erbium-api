package br.com.erbium.core;

public enum EItem {
    MESSAGE,
    MESSAGE_COMPLEMENT,
    REQUEST_METHOD,
    REQUEST_URL,
    REQUEST_HEADERS,
    REQUEST_BODY,
    RESPONSE_HEADERS,
    RESPONSE_BODY,
    RESPONSE_CODE,
    ENVIRONMENT_TABLE;

    // For custom items
    private static int customCounter = 0;

    public static EItem newCustomItem() {
        try {
            return valueOf("CUSTOM_" + customCounter++);
        } catch (IllegalArgumentException e) {
            // Handle case where enum values are exhausted
            throw new RuntimeException("Maximum custom items exceeded");
        }
    }
}
