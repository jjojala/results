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
import org.gemini.results.model.Clazz;
import org.gemini.results.model.Event;
import org.gemini.results.model.Competitor;
import org.gemini.results.model.CompetitorList;
import org.gemini.results.model.Group;
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.NameList;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompetitorResourceTest extends JerseyTest {
    private static String eventId;
    private static String groupId;
    private static String classAId, classBId;
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        eventId = UUID.randomUUID().toString();
        groupId = UUID.randomUUID().toString();
        classAId = UUID.randomUUID().toString();
        classBId = UUID.randomUUID().toString();

        emf = Persistence.createEntityManagerFactory("results-data");

        {
            final Event event = new Event(
                    eventId,
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-02-04T22:52:15.000+02:00"),
                    "my-name", "my-organizer", null, null, null);

            final Group group = new Group(groupId, eventId,
                    "my-group-name", (short) -1, (short) -1, 0L);

            final Clazz clazzA = new Clazz(classAId, eventId,
                    "A-class", 0L, groupId);
            final Clazz clazzB = new Clazz(classBId, eventId,
                    "B-class", 0L, groupId);

            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            trx.begin();
            DataUtils.create(em, eventId, event);
            DataUtils.create(em, groupId, group);
            DataUtils.create(em, classAId, clazzA);
            DataUtils.create(em, classBId, clazzB);
            trx.commit();

            DataUtils.close(em);
        }
    }

    @AfterClass
    public static void cleanUpClass() {
        final EntityManager em = emf.createEntityManager();

        try {
            final EntityTransaction trx = em.getTransaction();

            trx.begin();

            final List<Event> events =
                    em.createNamedQuery("Event.list").getResultList();

            for (final Event c: events) {
                em.remove(c);
            }

            trx.commit();
        }

        finally {
            DataUtils.close(em);
        }

        DataUtils.close(emf);
        emf = null;
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new EventResource(emf, null));
    }

    @Test
    public void testListNames() {
        final Competitor cA = new Competitor(UUID.randomUUID().toString(),
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);
        final Competitor cB = new Competitor(UUID.randomUUID().toString(),
                eventId, "Hiiri Mikki", classAId, (short) -1, 0L, 0L);
        final Competitor cC = new Competitor(UUID.randomUUID().toString(),
                eventId, "Hiiri Mikki", classBId, (short) -1, 0L, 0L);

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.create(em, cA.getId(), cA);
                DataUtils.create(em, cB.getId(), cB);
                DataUtils.create(em, cC.getId(), cC);
                trx.commit();
            } finally { DataUtils.close(em); }
        }

        final Response response = target(String.format(
                "events/%s/competitors/names", eventId)).request().get();
        Assert.assertEquals(200, response.getStatus());
        final List<String> names = response.readEntity(NameList.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("Ankka Aku", names.get(0));
        Assert.assertEquals("Hiiri Mikki", names.get(1));

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.remove(em, cA.getId(), Competitor.class);
                DataUtils.remove(em, cB.getId(), Competitor.class);
                DataUtils.remove(em, cC.getId(), Competitor.class);
                trx.commit();
            } finally { DataUtils.close(em); }
        }
    }

    @Test
    public void testGet() {
        final Competitor competitor = new Competitor(UUID.randomUUID().toString(),
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.create(em, competitor.getId(), competitor);
                trx.commit();
            } finally { DataUtils.close(em); }
        }

        final Response response = target(String.format(
                "events/%s/competitors/%s", eventId, competitor.getId()))
                .request().get();

        Assert.assertEquals(200, response.getStatus());
        final Competitor c = response.readEntity(Competitor.class);
        Assert.assertEquals(competitor.getId(), c.getId());

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.remove(em, competitor.getId(), Competitor.class);
                trx.commit();
            } finally { DataUtils.close(em); }
        }
    }

    @Test
    public void testList() {
        final Competitor cA = new Competitor(UUID.randomUUID().toString(),
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);
        final Competitor cB = new Competitor(UUID.randomUUID().toString(),
                eventId, "Hiiri Mikki", classAId, (short) -1, 0L, 0L);
        final Competitor cC = new Competitor(UUID.randomUUID().toString(),
                eventId, "Hiiri Mikki", classBId, (short) -1, 0L, 0L);

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.create(em, cA.getId(), cA);
                DataUtils.create(em, cB.getId(), cB);
                DataUtils.create(em, cC.getId(), cC);
                trx.commit();
            } finally { DataUtils.close(em); }
        }

        {
            final Response response = target(String.format(
                    "events/%s/competitors/", eventId)).request().get();
            Assert.assertEquals(200, response.getStatus());
            final List<Competitor> competitors =
                    response.readEntity(CompetitorList.class);
            Assert.assertEquals(3, competitors.size());
        }

        {
            final Response response = target(String.format(
                    "events/%s/competitors/", eventId))
                    .queryParam("classId", classBId).request().get();
            Assert.assertEquals(200, response.getStatus());
            final List<Competitor> competitors =
                    response.readEntity(CompetitorList.class);
            Assert.assertEquals(1, competitors.size());
            Assert.assertEquals(cC.getId(), competitors.get(0).getId());
        }

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.remove(em, cA.getId(), Competitor.class);
                DataUtils.remove(em, cB.getId(), Competitor.class);
                DataUtils.remove(em, cC.getId(), Competitor.class);
                trx.commit();
            } finally { DataUtils.close(em); }
        }
    }

    @Test
    public void testCreate() {
        final String competitorId = UUID.randomUUID().toString();

        { // event not found
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/huuhaa/competitors/%s", competitorId))
                    .request().post(Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
            Assert.assertTrue(body.contains(Event.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Class not found
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", "huuhaa", (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s",
                    eventId, competitorId))
                    .request().post(Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        {
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().post(
                    Entity.xml(c));

            Assert.assertEquals(201, response.getStatus());
        }

        { // re-create
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().post(
                    Entity.xml(c));

            Assert.assertEquals(409, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityExistsException"));
        }

        {
            final Response response = target(String.format(
                    "events/%s/competitors/%s",
                    eventId, competitorId)).request().get();
            Assert.assertEquals(200, response.getStatus());
            final Competitor c = response.readEntity(Competitor.class);
            Assert.assertNotNull(c);
            Assert.assertEquals(competitorId, c.getId());
        }

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.remove(em, competitorId, Competitor.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testUpdate() {
        final String competitorId = UUID.randomUUID().toString();

        {
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().post(
                    Entity.xml(c));

            Assert.assertEquals(201, response.getStatus());
        }

        { // event not found
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/huuhaa/competitors/%s", competitorId))
                    .request().put(Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
            Assert.assertTrue(body.contains(Event.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Class not found
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", "huuhaa", (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s",
                    eventId, competitorId))
                    .request().put(Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // competitor not found
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/huuhaa", 
                    eventId)).request().put(
                    Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
        }

        { // update succeeds
            final Competitor c = new Competitor(competitorId,
                eventId, "Jagger Mick", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().put(
                    Entity.xml(c));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final Response response = target(String.format(
                    "events/%s/competitors/%s",
                    eventId, competitorId)).request().get();
            Assert.assertEquals(200, response.getStatus());
            final Competitor c = response.readEntity(Competitor.class);
            Assert.assertNotNull(c);
            Assert.assertEquals(competitorId, c.getId());
            Assert.assertEquals("Jagger Mick", c.getName());
        }

        {
            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                DataUtils.remove(em, competitorId, Competitor.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testRemove() {
        final String competitorId = UUID.randomUUID().toString();

        {
            final Competitor c = new Competitor(competitorId,
                eventId, "Ankka Aku", classAId, (short) -1, 0L, 0L);

            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().post(
                    Entity.xml(c));

            Assert.assertEquals(201, response.getStatus());
        }

        { // event not found
            final Response response = target(String.format(
                    "events/huuhaa/competitors/%s", competitorId))
                    .request().delete();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
            Assert.assertTrue(body.contains(Event.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // competitor not found
            final Response response = target(String.format(
                    "events/%s/competitors/huuhaa", 
                    eventId)).request().delete();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("EntityNotFoundException"));
        }

        { // remove succeeds
            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().delete();

            Assert.assertEquals(200, response.getStatus());
        }

        { // re-remove fails
            final Response response = target(String.format(
                    "events/%s/competitors/%s", 
                    eventId, competitorId)).request().delete();

            Assert.assertEquals(404, response.getStatus());
        }
    }
}
