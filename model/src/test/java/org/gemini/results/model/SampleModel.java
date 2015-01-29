/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public class SampleModel {

    public static void main(final String[] args) {
        try {
            final Clazz
                    h21 = new Clazz(UUID.randomUUID().toString(),
                        "H21", duration("P0M"), null),
                    d21 = new Clazz(UUID.randomUUID().toString(),
                        "D21", duration("P30M"), null),
                    h35 = new Clazz(UUID.randomUUID().toString(),
                        "H35", duration("P15M"), null),
                    d35 = new Clazz(UUID.randomUUID().toString(),
                        "D35", duration("P45M"), null);

            final List<Competitor> competitors = new ArrayList<>();
            competitors.addAll(makeCompetitors("H21", 14));
            competitors.addAll(makeCompetitors("D21", 14));
            competitors.addAll(makeCompetitors("H35", 14));
            competitors.addAll(makeCompetitors("D35", 100));

            final List<Group> startGroups = Arrays.asList(
                    new Group(UUID.randomUUID().toString(),
                        "elite", (short)1, (short)99, duration("P0M")),
                    new Group(UUID.randomUUID().toString(),
                        "national", (short)100, (short)500, duration("P30M")));

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

    public static Duration duration(final String str) {
        return ModelUtils.getDatatypeFactory().newDuration(str);
    }

    public static List<Competitor> makeCompetitors(
            final String clazzName, final int count) {
        final List<Competitor> result = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            result.add(new Competitor(UUID.randomUUID().toString(),
                    String.format("Family-%s-%d Given-%d", clazzName, i, i),
                    null, (short)-1, null, null));
        }

        return result;
    }
}
