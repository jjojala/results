/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.gemini.results.model.ModelUtils;

@Converter
public class XMLGregorianCalendarConverter
        implements AttributeConverter<XMLGregorianCalendar, String> {

    @Override
    public String convertToDatabaseColumn(final XMLGregorianCalendar x) {
        return x.toXMLFormat();
    }

    @Override
    public XMLGregorianCalendar convertToEntityAttribute(final String y) {
        return ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(y);
    }
    
}
