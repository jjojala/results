/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Competition;
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.Group;
import org.gemini.results.model.GroupList;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GroupResourceTest extends JerseyTest {

    private static String competitionId;
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        competitionId = UUID.randomUUID().toString();
        emf = Persistence.createEntityManagerFactory("results-data");

        {
            final Competition competition = new Competition(
                    competitionId,
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-01-31T11:52:15.000+02:00"),
                    "my-name", "my-organizer", null, null, null);

            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            trx.begin();
            DataUtils.persist(em, competition);
            trx.commit();

            DataUtils.close(em);
        }
    }

    @AfterClass
    public static void cleanUpClass() {
        try { emf.close(); } catch (final Throwable ignored) {}
    }

    @After
    public void cleanUp() {
        final EntityManager em = emf.createEntityManager();

        try {
            final EntityTransaction trx = em.getTransaction();

            trx.begin();

            final List<Competition> competitions =
                    em.createNamedQuery("Competition.list").getResultList();

            for (final Competition c: competitions) {
                em.remove(c);
            }

            trx.commit();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new CompetitionResource(emf));
    }

    @Test
    public void testList() {
        {
            final String groupId = UUID.randomUUID().toString();
            {
                final Group group = new Group("blaah-blaah", "blaah-blaah",
                        "my-group-name", (short)-1, (short)-1, 0L);

                final Response response = target(String.format(
                        "competition/%s/group/%s", competitionId, groupId))
                        .request().post(Entity.xml(group));
                Assert.assertEquals(201, response.getStatus());
            }

            {
                final Response response = target(String.format(
                        "competition/%s/group", competitionId)).request().get();
                Assert.assertEquals(200, response.getStatus());
                final List<Group> groups = response.readEntity(GroupList.class);
                Assert.assertEquals(1, groups.size());
                final Group group = groups.get(0);
                Assert.assertEquals(groupId, group.getId());
                Assert.assertEquals(competitionId, group.getCompetitionId());
            }

            {
                final Response response = target(String.format(
                        "competition/%s/group/%s", competitionId, groupId))
                        .request().delete();
                Assert.assertEquals(200, response.getStatus());
            }
        }

        {
            final Response response = target(String.format(
                    "competition/%s/group", competitionId)).request().get();
            Assert.assertEquals(200, response.getStatus());
            final List<Group> groups = response.readEntity(GroupList.class);
            Assert.assertEquals(0, groups.size());
        }
    }

    @Test
    public void writeOtherTestCases() {
        Assert.fail();
    }
}
