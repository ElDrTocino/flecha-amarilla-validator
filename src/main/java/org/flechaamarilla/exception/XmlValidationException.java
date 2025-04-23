package org.flechaamarilla.exception;

import java.util.List;

public class XmlValidationException extends Exception {

    private final List<String> validationErrors;

    public XmlValidationException(List<String> errors) {
        super("Errores de validación XML");
        this.validationErrors = errors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}