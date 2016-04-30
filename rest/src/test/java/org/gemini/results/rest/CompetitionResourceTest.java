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
import org.gemini.results.model.Event;
import org.gemini.results.model.EventList;
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
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new EventResource(emf, null));
    }

    @Test
    public void testList() {
        final Event event = new Event(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T11:52:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        {   // Create a event (so that we have at least one event on
            // the list)
            final Response response = target("events/" + event.getId())
                    .request().post(Entity.json(event));
            Assert.assertEquals(201, response.getStatus());
        }

        {   // Get the just retrieved event
            final Response response = target("events/" + event.getId())
                    .request().accept("application/json").get();
            Assert.assertEquals(200, response.getStatus());

            final Event c = response.readEntity(Event.class);
            Assert.assertEquals(event.getId(), c.getId());
            Assert.assertEquals(event.getTime(), c.getTime());
        }

        { // Get the list of events - and remove them one by one
            final Response listResponse = target("events").request().get();
            Assert.assertEquals(200, listResponse.getStatus());

            final List<Event> events =
                    listResponse.readEntity(EventList.class);
            Assert.assertTrue(events.size() > 0);

            for (final Event c: events) {
                final Response deleteResponse = 
                        target("events/" + c.getId()).request().delete();
                Assert.assertEquals(200, deleteResponse.getStatus());
            }
        }

        { // Final get - nothing shouldn't returned (as we just removed them)
            final Response listResponse = target("events").request().get();
            Assert.assertEquals(200, listResponse.getStatus());

            final List<Event> events =
                    listResponse.readEntity(EventList.class);
            Assert.assertEquals(0, events.size());
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
        final Event event = new Event(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + event.getId()).request()
                    .post(Entity.xml(event));
            Assert.assertEquals(201, response.getStatus());
        }

        {
            final Response response = 
                    target("events/" + event.getId()).request()
                    .post(Entity.xml(event));
            Assert.assertEquals(409, response.getStatus());
        }
    }

    @Test
    public void testUpdate() {
        final Event event = new Event(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + event.getId()).request()
                    .post(Entity.xml(event));
            Assert.assertEquals(201, response.getStatus());
        }

        { // Update - should be fine..
            final Event c = target("events/" + event.getId())
                    .request().get(Event.class);

            Assert.assertEquals("my-name", c.getName());
            c.setName("my-new-name");
            final Response response = target("events/" + c.getId())
                    .request().put(Entity.xml(c));

            Assert.assertEquals(200, response.getStatus());
        }

        { // Update something that doesn't exist
            final String nonId = UUID.randomUUID().toString();

            final Event c = target("events/" + event.getId())
                    .request().get(Event.class);

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
        final Event event = new Event(
                UUID.randomUUID().toString(),
                ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-31T13:07:15.000+02:00"),
                "my-name", "my-organizer", null, null, null);

        { // Create first
            final Response response =
                    target("events/" + event.getId()).request()
                    .post(Entity.xml(event));
            Assert.assertEquals(201, response.getStatus());
        }

        { // Remove
            final Response response = target(
                    "events/" + event.getId()).request().delete();
            Assert.assertEquals(200, response.getStatus());
        }

        { // Remove (non-existing competation at this point)
            final Response response = target(
                    "events/" + event.getId()).request().delete();
            Assert.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testNameAttribute() {
        final String id = UUID.randomUUID().toString();

        {
            final Event event = new Event("my-id-overriden",
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-19T22:45:15.000+02:00"),
                    "my-test-competition", "my-athletic-club", null, null, null);

            final WebTarget manager = target(String.format(
                    "events/%s", id));

            final Response response = manager.request().post(
                    Entity.xml(event));

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
        final String eventId = UUID.randomUUID().toString();
        final String startGroupId = UUID.randomUUID().toString();

        {
            final Event event = new Event("my-id-overriden",
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-01-19T22:45:15.000+02:00"),
                    "my-test-competition", "my-athletic-club", null, null, null);

            final WebTarget manager = target(String.format(
                    "events/%s", eventId));

            final Response response = manager.request().post(
                    Entity.xml(event));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final Group group = new Group(startGroupId, eventId,
                    "my-start-group-name", (short)-1, (short)-1, 0L);

            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s",
                    eventId, startGroupId));

            final Response response = manager.request().post(
                    Entity.xml(group));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    eventId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-start-group-name",
                    response.readEntity(String.class));
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    eventId, startGroupId));

            final Response response = manager.request().put(
                    Entity.xml("my-new-start-group-name"));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final WebTarget manager = target(String.format(
                    "events/%s/groups/%s#name",
                    eventId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-new-start-group-name",
                    response.readEntity(String.class));
        }

    }
}
