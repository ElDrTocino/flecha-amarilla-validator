package org.flechaamarilla.messaging;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.flechaamarilla.exception.XmlValidationException;
import org.flechaamarilla.service.XmlValidatorService;

@ApplicationScoped
@Slf4j
public class BillProcessor {


    @Inject
    XmlValidatorService validatorService;

    @Incoming("facturas-procesadas")
    @Outgoing("facturas-validadas")
    @Blocking
    public String processBill(String xmlContent) {
        try {
            log.info("Validating XML from queue");
            validatorService.validateCfdi(xmlContent);
            log.info("XML is valid, forwarding to output queue");
            return xmlContent;  // Forward valid XML to output channel
        } catch (XmlValidationException e) {
            log.warn("XML validation failed: " + String.join(", ", e.getValidationErrors()));
            return null;  // Don't forward invalid XML
        } catch (Exception e) {
            log.error("Error processing XML: " + e.getMessage(), e);
            return null;  // Don't forward on error
        }
    }
}