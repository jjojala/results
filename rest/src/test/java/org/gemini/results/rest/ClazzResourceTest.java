/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Clazz;
import org.gemini.results.model.ClazzList;
import org.gemini.results.model.Competition;
import org.gemini.results.model.Group;
import org.gemini.results.model.ModelUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClazzResourceTest extends JerseyTest {
    private static String competitionId;
    private static String groupId;
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        competitionId = UUID.randomUUID().toString();
        groupId = UUID.randomUUID().toString();

        emf = Persistence.createEntityManagerFactory("results-data");

        {
            final Competition competition = new Competition(
                    competitionId,
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-02-04T22:52:15.000+02:00"),
                    "my-name", "my-organizer", null, null, null);

            final Group group = new Group(groupId, competitionId,
                    "my-group-name", (short) -1, (short) -1, 0L);

            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            trx.begin();
            DataUtils.create(em, competitionId, competition);
            DataUtils.create(em, groupId, group);
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

        DataUtils.close(emf);
        emf = null;
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new EventResource(emf, null));
    }

    @Test
    public void testList() {
        final Clazz clazzWithGroup = new Clazz(UUID.randomUUID().toString(),
                competitionId, "a-my-class", 0L, groupId);
        final Clazz clazzWithoutGroup = new Clazz(UUID.randomUUID().toString(),
                competitionId, "b-my-class", 0L, null);

        { // setup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.create(em, clazzWithGroup.getId(), clazzWithGroup);
                DataUtils.create(em, clazzWithoutGroup.getId(), clazzWithoutGroup);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }

        {
            final Response response = target(String.format(
                    "events/%s/classes", competitionId)).request().get();

            Assert.assertEquals(200, response.getStatus());
            final List<Clazz> classes = response.readEntity(ClazzList.class);
            Assert.assertEquals(2, classes.size());
            Assert.assertEquals("a-my-class", classes.get(0).getName());
            Assert.assertEquals("b-my-class", classes.get(1).getName());
        }

        {
            final Response response = target(String.format(
                    "events/%s/classes", competitionId)).queryParam(
                        "groupId", groupId).request().get();

            Assert.assertEquals(200, response.getStatus());
            final List<Clazz> classes = response.readEntity(ClazzList.class);
            Assert.assertEquals(1, classes.size());
            Assert.assertEquals("a-my-class", classes.get(0).getName());
        }

        {
            final Response response = target(String.format(
                    "events/%s/classes", competitionId)).queryParam(
                    "groupId", "huuhaa").request().get();

            Assert.assertEquals(200, response.getStatus());
            final List<Clazz> classes = response.readEntity(ClazzList.class);
            Assert.assertEquals(0, classes.size());
        }

        {
            final Response response = target(String.format(
                    "events/huuhaa/classes")).request().get();
            Assert.assertEquals(404, response.getStatus());
            System.out.println("Body: " + response.readEntity(String.class));
        }

        { // cleanup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.remove(em, clazzWithGroup.getId(), Clazz.class);
                DataUtils.remove(em, clazzWithoutGroup.getId(), Clazz.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testGet() {
        final Clazz clazz = new Clazz(UUID.randomUUID().toString(),
                competitionId, "a-my-class", 0L, groupId);

        { // setup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.create(em, clazz.getId(), clazz);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }

        {
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazz.getId()))
                    .request().get();

            Assert.assertEquals(200, response.getStatus());
            final Clazz c = response.readEntity(Clazz.class);
            Assert.assertNotNull(c);
            Assert.assertEquals(clazz.getId(), c.getId());
        }

        { // class not found
            final Response response = target(String.format(
                    "events/%s/classes/huuhaa", competitionId))
                    .request().get();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // class not found
            final Response response = target(String.format(
                    "events/huuhaa/classes/%s", clazz.getId()))
                    .request().get();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Competition.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // cleanup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.remove(em, clazz.getId(), Clazz.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testCreate() {
        final String validClassId = UUID.randomUUID().toString();

        {
            final Clazz clazz = new Clazz(validClassId,
                    competitionId, "a-my-class", 0L, groupId);

            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, validClassId))
                    .request().post(Entity.xml(clazz));

            Assert.assertEquals(201, response.getStatus());
        }

        { // re-create
            final Clazz clazz = new Clazz(validClassId,
                    competitionId, "a-my-class", 0L, groupId);

            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, validClassId))
                    .request().post(Entity.xml(clazz));

            Assert.assertEquals(409, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityExistsException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains(validClassId));
        }

        { // Competition not found
            final Clazz clazz = new Clazz(validClassId,
                    "huuhaa", "a-my-class", 0L, groupId);

            final Response response = target(String.format(
                    "events/huuhaa/classes/%s", validClassId))
                    .request().post(Entity.xml(clazz));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Competition.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Group not found
            final Clazz clazz = new Clazz(validClassId,
                    competitionId, "a-my-class", 0L, "huuhaa");

            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, validClassId))
                    .request().post(Entity.xml(clazz));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Group.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // cleanup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.remove(em, validClassId, Clazz.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testUpdate() {
        final String clazzId = UUID.randomUUID().toString();
        final Clazz clazz = new Clazz(clazzId,
                competitionId, "a-my-class", 0L, groupId);

        { // Create
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().post(Entity.xml(clazz));
            Assert.assertEquals(201, response.getStatus());
        }

        { // update
            clazz.setName("b-my-class");
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().put(Entity.xml(clazz));
            Assert.assertEquals(200, response.getStatus());
        }

        { // Verify
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().get();
            Assert.assertEquals(200, response.getStatus());
            final Clazz c = response.readEntity(Clazz.class);
            Assert.assertEquals(clazz.getName(), c.getName());
        }

        { // Competition not found
            clazz.setName("never-committed");
            final Response response = target(String.format(
                    "events/huuhaa/classes/%s", clazzId))
                    .request().put(Entity.xml(clazz));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Competition.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Group not found
            clazz.setGroupId("huuhaa");
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().put(Entity.xml(clazz));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Group.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Class not found
            clazz.setGroupId(null);
            clazz.setName("never-committed");
            final Response response = target(String.format(
                    "events/%s/classes/huuhaa", competitionId))
                    .request().put(Entity.xml(clazz));

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            System.out.println("Body: " + body);
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // cleanup
            final EntityManager em = emf.createEntityManager();
            try {
                final EntityTransaction trx = em.getTransaction();
                trx.begin();
                DataUtils.remove(em, clazzId, Clazz.class);
                trx.commit();
            } finally {
                DataUtils.close(em);
            }
        }
    }

    @Test
    public void testRemove() {
        final String clazzId = UUID.randomUUID().toString();
        final Clazz clazz = new Clazz(clazzId,
                competitionId, "a-my-class", 0L, groupId);

        { // Create
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().post(Entity.xml(clazz));
            Assert.assertEquals(201, response.getStatus());
        }

        { // Verify
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().get();
            Assert.assertEquals(200, response.getStatus());
            final Clazz c = response.readEntity(Clazz.class);
            Assert.assertEquals(clazz.getName(), c.getName());
        }

        { // Competition not found
            final Response response = target(String.format(
                    "events/huuhaa/classes/%s", clazzId))
                    .request().delete();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Competition.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // Class not found
            final Response response = target(String.format(
                    "events/%s/classes/huuhaa", competitionId))
                    .request().delete();

            Assert.assertEquals(404, response.getStatus());
            final String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(body.contains(Clazz.class.getName()));
            Assert.assertTrue(body.contains("huuhaa"));
        }

        { // actual removal
            final Response response = target(String.format(
                    "events/%s/classes/%s", competitionId, clazzId))
                    .request().delete();
            Assert.assertEquals(200, response.getStatus());
        }
    }
}
