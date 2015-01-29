/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.gemini.results.model.ModelUtils;

@Converter
public class XMLGregorianCalendarConverter
        implements AttributeConverter<XMLGregorianCalendar, Timestamp> {

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    @Override
    public Timestamp convertToDatabaseColumn(final XMLGregorianCalendar x) {
        return x == null 
                ? null
                : new Timestamp(x.toGregorianCalendar().getTimeInMillis());
    }

    @Override
    public XMLGregorianCalendar convertToEntityAttribute(final Timestamp y) {
        if (y != null) {
            final GregorianCalendar cal = (GregorianCalendar)
                    GregorianCalendar.getInstance(GMT);
            cal.setTimeInMillis(y.getTime());
            return ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(cal);
        }

        return null;
    }
}
