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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Competition;
import org.gemini.results.model.CompetitionList;
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.Group;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class CompetitionResourceTest extends JerseyTest {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("results-data");

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
        return new ResourceConfig().register(new EventResource(emf, null));
    }

    @Test
    public void testList() {
        final Competition competition = new Competition(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T11:52:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        {   // Create a competiont (so that we have at least one competition on
            // the list)
            final Response response = target("events/" + competition.getId())
                    .request().post(Entity.json(competition));
            Assert.assertEquals(201, response.getStatus());
        }

        {   // Get the just retrieved competition
            final Response response = target("events/" + competition.getId())
                    .request().accept("application/json").get();
            Assert.assertEquals(200, response.getStatus());

            final Competition c = response.readEntity(Competition.class);
            Assert.assertEquals(competition.getId(), c.getId());
            Assert.assertEquals(competition.getTime(), c.getTime());
        }

        { // Get the list of competitions - and remove them one by one
            final Response listResponse = target("events").request().get();
            Assert.assertEquals(200, listResponse.getStatus());

            final List<Competition> competitions =
                    listResponse.readEntity(CompetitionList.class);
            Assert.assertTrue(competitions.size() > 0);

            for (final Competition c: competitions) {
                final Response deleteResponse = 
                        target("events/" + c.getId()).request().delete();
                Assert.assertEquals(200, deleteResponse.getStatus());
            }
        }

        { // Final get - nothing shouldn't returned (as we just removed them)
            final Response listResponse = target("events").request().get();
            Assert.assertEquals(200, listResponse.getStatus());

            final List<Competition> competitions =
                    listResponse.readEntity(CompetitionList.class);
            Assert.assertEquals(0, competitions.size());
        }
    }

    @Test
    public void testGetNotFound() {
        final String nonId = UUID.randomUUID().toString();

        final Response response =
                target("events/" + nonId).request().get();
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testCreate() {
        final Competition competition = new Competition(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + competition.getId()).request()
                    .post(Entity.xml(competition));
            Assert.assertEquals(201, response.getStatus());
        }

        {
            final Response response = 
                    target("events/" + competition.getId()).request()
                    .post(Entity.xml(competition));
            Assert.assertEquals(409, response.getStatus());
        }
    }

    @Test
    public void testUpdate() {
        final Competition competition = new Competition(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + competition.getId()).request()
                    .post(Entity.xml(competition));
            Assert.assertEquals(201, response.getStatus());
        }

        { // Update - should be fine..
            final Competition c = target("events/" + competition.getId())
                    .request().get(Competition.class);

            Assert.assertEquals("my-name", c.getName());
            c.setName("my-new-name");
            final Response response = target("events/" + c.getId())
                    .request().put(Entity.xml(c));

            Assert.assertEquals(200, response.getStatus());
        }

        { // Update something that doesn't exist
            final String nonId = UUID.randomUUID().toString();

            final Competition c = target("events/" + competition.getId())
                    .request().get(Competition.class);

            Assert.assertEquals("my-new-name", c.getName());
            Assert.assertEquals("my-organizer", c.getOrganizer());

            c.setOrganizer("my-new-organizer");

            final Response response = target("events/" + nonId)
                    .request().put(Entity.xml(c));

            Assert.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testRemove() {
        final Competition competition = new Competition(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + competition.getId()).request()
                    .post(Entity.xml(competition));
            Assert.assertEquals(201, response.getStatus());
        }

        { // Remove
            final Response response = target(
                    "events/" + competition.getId()).request().delete();
            Assert.assertEquals(200, response.getStatus());
        }

        { // Remove (non-existing competation at this point)
            final Response response = target(
                    "events/" + competition.getId()).request().delete();
            Assert.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testNameAttribute() {
        final String id = UUID.randomUUID().toString();

        {
            final Competition competition = new Competition("my-id-overriden",
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-19T22:45:15.000+02:00"),
                    "my-test-competition", "my-athletic-club", null, null, null);

            final WebTarget manager = target(String.format(
                    "events/%s", id));

            final Response response = manager.request().post(
                    Entity.xml(competition));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s#name", id));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-test-competition",
                    response.readEntity(String.class));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s#name", id));

            final Response response = manager.request().put(
                    Entity.xml("my-new-competition-name"));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s#name", id));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-new-competition-name",
                    response.readEntity(String.class));
        }
    }

    @Test
    public void testStartGroup() {
        final String competitionId = UUID.randomUUID().toString();
        final String startGroupId = UUID.randomUUID().toString();

        {
            final Competition competition = new Competition("my-id-overriden",
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-01-19T22:45:15.000+02:00"),
                    "my-test-competition", "my-athletic-club", null, null, null);

            final WebTarget manager = target(String.format(
                    "events/%s", competitionId));

            final Response response = manager.request().post(
                    Entity.xml(competition));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final Group group = new Group(startGroupId, competitionId,
                    "my-start-group-name", (short)-1, (short)-1, 0L);

            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s",
                    competitionId, startGroupId));

            final Response response = manager.request().post(
                    Entity.xml(group));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-start-group-name",
                    response.readEntity(String.class));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().put(
                    Entity.xml("my-new-start-group-name"));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-new-start-group-name",
                    response.readEntity(String.class));
        }

    }
}
