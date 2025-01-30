package com.repnox.nineseventax.features.efile.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class ValidationErrorListConverter implements AttributeConverter<List<ValidationError>, String> {

    @Override
    public String convertToDatabaseColumn(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(errors);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public List<ValidationError> convertToEntityAttribute(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value, new TypeReference<List<ValidationError>>() {});
        } catch (IOException ex) {
            return null;
        }
    }

}
