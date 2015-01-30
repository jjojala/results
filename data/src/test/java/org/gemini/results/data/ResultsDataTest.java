/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.gemini.results.model.Clazz;
import org.gemini.results.model.Competition;
import org.gemini.results.model.Competitor;
import org.gemini.results.model.Group;
import org.gemini.results.model.ModelUtils;
import org.junit.Test;

public class ResultsDataTest {

    @Test
    public void testDatabase() throws Exception {
        final EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("results-data");

        try {
            EntityManager em = emf.createEntityManager();

            final Competition competition = new Competition(
                    UUID.randomUUID().toString(),
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-28T22:20:00.000+02:00"),
                    "Testikisa", "Meidän poppoo", null, null, null);

            final Group group = new Group(
                    UUID.randomUUID().toString(), competition.getId(),
                    "group", (short)-1, (short)-1, 0L);

            final Clazz assignedClazz = new Clazz(
                    UUID.randomUUID().toString(), competition.getId(), "H21",
                    1000L*60*1 /* 1 minute */, group.getId());

            final Clazz unassignedClazz = new Clazz(
                    UUID.randomUUID().toString(), competition.getId(), "H21",
                    1000L*60*1 /* 1 minute */, null /* groupId */);

            final Competitor competitor = new Competitor(
                    UUID.randomUUID().toString(), competition.getId(),
                    "Trump Donald", assignedClazz.getId(),
                    (short)0, null, null);

            EntityTransaction trx = em.getTransaction();
            trx.begin();
            em.persist(competition);
            em.persist(group);
            em.persist(assignedClazz);
            em.persist(unassignedClazz);
            em.persist(competitor);
            trx.commit();
            em.close();

            System.out.println("Ready!");
        }

        finally {
            try { emf.close(); } catch (final Throwable ignored) {}
        }
    }
}
