package org.flechaamarilla.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class XmlValidationException extends Exception {

    private final List<String> validationErrors;

    public XmlValidationException(List<String> errors) {
        super("Errores de validación XML");
        this.validationErrors = errors;
    }

}