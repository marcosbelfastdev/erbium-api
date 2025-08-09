package br.com.erbium.exceptions;

public class DuplicateIdentifier extends RuntimeException {
    public DuplicateIdentifier(String identifier) {
        super("There has been an attempt to create an instance with the name of an existing instance: " + identifier);
    }
    public DuplicateIdentifier(String message, String identifier) {
        super(message + ": " + identifier);
    }

}
