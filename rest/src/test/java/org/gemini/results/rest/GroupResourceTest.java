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
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.Group;
import org.gemini.results.model.GroupList;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GroupResourceTest extends JerseyTest {

    private static String eventId;
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        eventId = UUID.randomUUID().toString();
        emf = Persistence.createEntityManagerFactory("results-data");

        {
            final Event event = new Event(
                    eventId,
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    "2015-01-31T11:52:15.000+02:00"),
                    "my-name", "my-organizer", null, null, null);

            final EntityManager em = emf.createEntityManager();
            final EntityTransaction trx = em.getTransaction();
            trx.begin();
            em.persist(event);
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
    public void testList() {
        {
            final String groupId = UUID.randomUUID().toString();
            {
                final Group group = new Group("blaah-blaah", "blaah-blaah",
                        "my-group-name", (short)-1, (short)-1, 0L);

                final Response response = target(String.format(
                        "events/%s/groups/%s", eventId, groupId))
                        .request().post(Entity.xml(group));
                Assert.assertEquals(201, response.getStatus());
            }

            {
                final Response response = target(String.format(
                        "events/%s/groups", eventId)).request().get();
                Assert.assertEquals(200, response.getStatus());
                final List<Group> groups = response.readEntity(GroupList.class);
                Assert.assertEquals(1, groups.size());
                final Group group = groups.get(0);
                Assert.assertEquals(groupId, group.getId());
                Assert.assertEquals(eventId, group.getEventId());
            }

            {
                final Response response = target(String.format(
                        "events/%s/groups/%s", eventId, groupId))
                        .request().delete();
                Assert.assertEquals(200, response.getStatus());
            }
        }

        {
            final Response response = target(String.format(
                    "events/%s/groups", eventId)).request().get();
            Assert.assertEquals(200, response.getStatus());
            final List<Group> groups = response.readEntity(GroupList.class);
            Assert.assertEquals(0, groups.size());
        }

        {
            final Response response = target("events/huuhaa/groups")
                    .request().get();
            Assert.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testNonExistingEvent() {
        final String nonExistingEventId = UUID.randomUUID().toString();
        final String nonExistingGroupId = UUID.randomUUID().toString();

        final Group group = new Group(nonExistingGroupId,
                nonExistingEventId, "my-group-name",
                (short)-1, (short)-1, 0L);

        { // Get
            final Response response = target(String.format(
                    "events/%s/groups/%s", nonExistingEventId,
                    nonExistingGroupId)).request().get();

            Assert.assertEquals(404, response.getStatus());
        }

        { // create
            final Response response = target(String.format(
                    "events/%s/groups/%s", nonExistingEventId,
                    nonExistingGroupId)).request().post(Entity.xml(group));

            System.out.println(response.getEntity());
            Assert.assertEquals(404, response.getStatus());
        }

        { // update
            final Response response = target(String.format(
                    "events/%s/groups/%s", nonExistingEventId,
                    nonExistingGroupId)).request().put(Entity.xml(group));

            Assert.assertEquals(404, response.getStatus());
        }

        { // delete
            final Response response = target(String.format(
                    "events/%s/groups/%s", nonExistingEventId,
                    nonExistingGroupId)).request().delete();

            Assert.assertEquals(404, response.getStatus());
            System.out.println(response.readEntity(String.class));
        }
    }

    @Test
    public void testNonExistingGroup() {
        final String nonExistingGroupId = UUID.randomUUID().toString();
        final Group group = new Group(nonExistingGroupId,
                eventId, "my-group-name",
                (short)-1, (short)-1, 0L);

        { // get
            final Response response = target(String.format(
                    "events/%s/groups/%s", eventId,
                    nonExistingGroupId)).request().get();

            Assert.assertEquals(404, response.getStatus());
        }

        { // update
            final Response response = target(String.format(
                    "events/%s/groups/%s", eventId,
                    nonExistingGroupId)).request().put(Entity.xml(group));

            Assert.assertEquals(404, response.getStatus());

            /* TODO: support for resonse body in cases of 4xx responses...
            final String responseBody = response.readEntity(String.class);
            System.out.println(responseBody);

            Assert.assertTrue(responseBody.startsWith(
                    EntityNotFoundException.class.getSimpleName()));
            Assert.assertTrue(responseBody.contains(
                    Group.class.getName()));
            Assert.assertTrue(responseBody.contains(
                    nonExistingGroupId));
                    */
        }

        { // delete
            final Response response = target(String.format(
                    "events/%s/groups/%s", eventId,
                    nonExistingGroupId)).request().delete();

            Assert.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testCrud() {
        final String groupId = UUID.randomUUID().toString();

        { // Create
            final Group group = new Group(groupId,
                    eventId, "my-group-name",
                    (short) -1, (short) -1, 0L);

            final WebTarget resource = target(String.format(
                    "events/%s/groups/%s", eventId, groupId));
            final Response response =
                    resource.request().post(Entity.xml(group));

            Assert.assertEquals(201, response.getStatus());
            final String locationHeader = response.getHeaderString("Location");
            Assert.assertTrue(locationHeader.contains(eventId));
            Assert.assertTrue(locationHeader.contains(groupId));
        }

        { // re-create
            final Group group = new Group(groupId,
                    eventId, "my-group-name",
                    (short) -1, (short) -1, 0L);

            final WebTarget resource = target(String.format(
                    "events/%s/groups/%s", eventId, groupId));
            final Response response =
                    resource.request().post(Entity.xml(group));

            Assert.assertEquals(409, response.getStatus());
        }

        { // Update
            { // baseline
                final Response response = target(String.format(
                        "events/%s/groups/%s", eventId, groupId))
                        .request().get();

                Assert.assertEquals(200, response.getStatus());
                final Group group = response.readEntity(Group.class);
                Assert.assertEquals("my-group-name", group.getName());
            }

            { // change and update
                final Group group = new Group(groupId,
                        eventId, "my-group-name#2",
                        (short) -1, (short) -1, 0L);

                final Response response = target(String.format(
                        "events/%s/groups/%s", eventId,
                        groupId)).request().put(Entity.xml(group));

                Assert.assertEquals(200, response.getStatus());
            }

            { // get changes
                final Response response = target(String.format(
                        "events/%s/groups/%s", eventId, groupId))
                        .request().get();

                Assert.assertEquals(200, response.getStatus());
                final Group group = response.readEntity(Group.class);
                Assert.assertEquals("my-group-name#2", group.getName());
            }
        }

        { // delete
            final Response response = target(String.format(
                    "events/%s/groups/%s", eventId, groupId))
                    .request().delete();

            Assert.assertEquals(200, response.getStatus());
        }
    }
}
