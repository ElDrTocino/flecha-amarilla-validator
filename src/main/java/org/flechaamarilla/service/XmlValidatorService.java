package org.flechaamarilla.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import org.flechaamarilla.exception.XmlValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class XmlValidatorService {

    private static final String CFDI_40_XSD = "xsd/cfdi.xsd";
    private static Schema cachedSchema;
    private static LocalDateTime lastCacheRefresh;
    // Tiempo de expiración del caché en horas (configurable según necesidades)
    private static final long CACHE_EXPIRATION_HOURS = 24;

    @PostConstruct
    public void init() {
        refreshSchemaCache();
    }

    /**
     * Refresca el caché del esquema XSD
     */
    public synchronized void refreshSchemaCache() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            cachedSchema = schemaFactory.newSchema(
                    new StreamSource(getClass().getClassLoader().getResourceAsStream(CFDI_40_XSD))
            );
            lastCacheRefresh = LocalDateTime.now();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el esquema XSD", e);
        }
    }

    /**
     * Obtiene el esquema XSD cacheado o refresca el caché si ha expirado
     */
    private Schema getSchema() {
        if (cachedSchema == null || isCacheExpired()) {
            refreshSchemaCache();
        }
        return cachedSchema;
    }

    /**
     * Verifica si el caché ha expirado
     */
    private boolean isCacheExpired() {
        if (lastCacheRefresh == null) {
            return true;
        }
        return lastCacheRefresh.plusHours(CACHE_EXPIRATION_HOURS).isBefore(LocalDateTime.now());
    }

    public void validateCfdi(String xmlContent) throws Exception {
        Validator validator = getSchema().newValidator();
        ValidationErrorCollector errorCollector = new ValidationErrorCollector();
        validator.setErrorHandler(errorCollector);

        validator.validate(new StreamSource(new StringReader(xmlContent)));

        if (!errorCollector.getErrors().isEmpty()) {
            throw new XmlValidationException(errorCollector.getErrors());
        }
    }

    // Clase para recolectar errores
    private static class ValidationErrorCollector implements ErrorHandler {
        private final List<String> errors = new ArrayList<>();

        @Override
        public void warning(SAXParseException exception) {
            errors.add(formatError("WARNING", exception));
        }

        @Override
        public void error(SAXParseException exception) {
            errors.add(formatError("ERROR", exception));
        }

        @Override
        public void fatalError(SAXParseException exception) {
            errors.add(formatError("FATAL", exception));
        }

        public List<String> getErrors() {
            return errors;
        }

        private String formatError(String level, SAXParseException ex) {
            return String.format("[%s] Línea %d, columna %d: %s", level, ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        }
    }
}