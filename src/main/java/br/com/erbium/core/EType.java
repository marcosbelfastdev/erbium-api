package br.com.erbium.core;

public enum EType {

    UDEF,
    INFO,
    LIGHT_WARNING,// Not likely to cause errors
    WARNING, // May cause errors
    SEVERE_WARNING, // Likely to cause errors
    INTERNAL_ERROR,
    ERROR;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}
