package br.com.erbium.exceptions;

public class LockedFeature extends RuntimeException {
    public LockedFeature(String identifier) {
        super("Attempt to modify or delete a feature that had been locked: " + identifier);
    }

    public LockedFeature(String message, String identifier) {
        super(message + ": " + identifier);
    }

}
