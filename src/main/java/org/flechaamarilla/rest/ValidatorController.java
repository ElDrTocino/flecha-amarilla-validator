package org.flechaamarilla.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.flechaamarilla.dto.XmlRequestDTO;
import org.flechaamarilla.dto.XmlResponseDTO;
import org.flechaamarilla.exception.XmlValidationException;
import org.flechaamarilla.service.XmlValidatorService;
import org.jboss.logging.Logger;

import java.util.ArrayList;

/**
 * REST endpoint for XML validation
 *
 */
@Path("/api/validator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValidatorController {
    
    private static final Logger LOG = Logger.getLogger(ValidatorController.class);
    
    @Inject
    XmlValidatorService validatorService;
    
    /**
     * Validates an XML document against the CFDI schema
     */
    @POST
    @Path("/validate")
    public XmlResponseDTO validateXml(XmlRequestDTO request) {
        LOG.info("Received validation request");
        
        XmlResponseDTO response = XmlResponseDTO.builder()
                .valid(true)
                .xmlContent(request.getXmlContent())
                .errors(new ArrayList<>())
                .build();
        
        try {
            // Validate the XML
            validatorService.validateCfdi(request.getXmlContent());
            LOG.info("XML validation successful");
            
        } catch (XmlValidationException e) {
            // XML is invalid but validation completed
            response.setValid(false);
            response.getErrors().addAll(e.getValidationErrors());
            LOG.warn("XML validation failed with errors: " + e.getValidationErrors());
            
        } catch (Exception e) {
            // Unexpected error during validation
            response.setValid(false);
            response.getErrors().add("Error during validation: " + e.getMessage());
            LOG.error("Error during XML validation", e);
        }
        
        return response;
    }
}