/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.datatype.Duration;
import org.gemini.results.model.ModelUtils;

@Converter
public final class DurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(final Duration duration) {
        return duration.toString();
    }

    @Override
    public Duration convertToEntityAttribute(final String durationString) {
        return ModelUtils.getDatatypeFactory().newDuration(durationString);
    }
}
