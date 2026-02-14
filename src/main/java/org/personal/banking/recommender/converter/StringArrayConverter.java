package org.personal.banking.recommender.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringArrayConverter implements AttributeConverter<List<String>, String[]> {

    @Override
    public String[] convertToDatabaseColumn(List<String> attribute) {
        return attribute != null ? attribute.toArray(new String[0]) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String[] dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(dbData);
    }
}