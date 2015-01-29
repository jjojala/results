/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

public class SampleModel {

    public static void main(final String[] args) {
        try {
            final Clazz
                    h21 = new Clazz(UUID.randomUUID().toString(), null,
                        "H21", 0L, null),
                    d21 = new Clazz(UUID.randomUUID().toString(), null,
                        "D21", 1000L*30*60 /* 30 minutes */, null),
                    h35 = new Clazz(UUID.randomUUID().toString(), null,
                        "H35", 1000L*15*60 /* 15 minutes */, null),
                    d35 = new Clazz(UUID.randomUUID().toString(), null,
                        "D35", 1000L*45*60 /* 45 minutes */, null);

            final List<Competitor> competitors = new ArrayList<>();
            competitors.addAll(makeCompetitors("H21", 14));
            competitors.addAll(makeCompetitors("D21", 14));
            competitors.addAll(makeCompetitors("H35", 14));
            competitors.addAll(makeCompetitors("D35", 100));

            final List<Group> startGroups = Arrays.asList(
                    new Group(UUID.randomUUID().toString(), null,
                        "elite", (short)1, (short)99, 0L),
                    new Group(UUID.randomUUID().toString(), null,
                        "national", (short)100, (short)500,
                        1000L*30*60 /* 60 mins */));

            final Competition competition = new Competition(
                    UUID.randomUUID().toString(),
                    time("2015-01-15T19:00:00.000+02:00"),
                    "Just local race", "Pirkkala Athletic Club",
                    startGroups, Arrays.asList(h21, d21, h35, d35),
                    competitors);

            final Marshaller marshaller =
                    ModelUtils.getJaxbContext().createMarshaller();
            marshaller.setProperty(
                    Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(competition, System.out);
        }

        catch (final Throwable ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static XMLGregorianCalendar time(final String str) {
        return ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(str);
    }

    public static List<Competitor> makeCompetitors(
            final String clazzName, final int count) {
        final List<Competitor> result = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            result.add(new Competitor(UUID.randomUUID().toString(), null,
                    String.format("Family-%s-%d Given-%d", clazzName, i, i),
                    null, (short)-1, null, null));
        }

        return result;
    }
}
