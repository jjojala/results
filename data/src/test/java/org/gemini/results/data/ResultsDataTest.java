/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.gemini.results.model.Competition;
import org.gemini.results.model.ModelUtils;

public class ResultsDataTest {

    public static void main(final String[] args) {
        EntityTransaction trx = null;
        try {
            final EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory("results-data");

            final EntityManager em = emf.createEntityManager();

            final Competition competition = new Competition(
                    UUID.randomUUID().toString(),
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-28T22:20:00.000+02:00"),
                    "Testikisa", "Meidän poppoo", null, null, null);

            trx = em.getTransaction();
            trx.begin();
            em.persist(competition);
            trx.commit();
        }

        catch (final Throwable ex) {
            try { trx.rollback(); } catch (final Throwable ignored) {};

            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
