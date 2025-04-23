package org.flechaamarilla.source;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.flechaamarilla.exception.XmlValidationException;
import org.flechaamarilla.service.XmlValidatorService;

import java.util.Map;

@Path("/validate")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ValidatorResource {

    @Inject
    XmlValidatorService validatorService;

    @POST
    public Response validateXml(String xmlContent) {
        try {
            validatorService.validateCfdi(xmlContent);
            return Response.ok(Map.of("valid", true)).build();
        } catch (XmlValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "valid", false,
                            "errors", e.getValidationErrors()
                    )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("valid", false, "error", e.getMessage()))
                    .build();
        }
    }
}