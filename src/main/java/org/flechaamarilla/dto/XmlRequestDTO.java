package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for XML processing requests
 * 
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XmlRequestDTO {
    private String xmlContent;
}