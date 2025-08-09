package br.com.erbium.exceptions;

public class IdentifierNotFound extends RuntimeException {
    public IdentifierNotFound(String identifier) {
        super("The following identifier was not found: " + identifier);
    }
    public IdentifierNotFound(String message, String identifier) {
        super(message + ": " + identifier);
    }

}
