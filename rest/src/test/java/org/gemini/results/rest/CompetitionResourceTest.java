/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.gemini.results.model.Competition;
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.Group;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class CompetitionResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(CompetitionResource.class);
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
                    "competition/%s", id));

            final Response response = manager.request().post(
                    Entity.xml(competition));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s#name", id));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-test-competition",
                    response.readEntity(String.class));
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s#name", id));

            final Response response = manager.request().put(
                    Entity.xml("my-new-competition-name"));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s#name", id));

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
                    "competition/%s", competitionId));

            final Response response = manager.request().post(
                    Entity.xml(competition));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final Group group = new Group(startGroupId,
                    "my-start-group-name", (short)-1, (short)-1,
                    ModelUtils.getDatatypeFactory().newDuration(0),
                    null);

            final WebTarget manager = target(String.format(
                    "competition/%s/group/%s",
                    competitionId, startGroupId));

            final Response response = manager.request().post(
                    Entity.xml(group));

            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(manager.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s/group/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-start-group-name",
                    response.readEntity(String.class));
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s/group/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().put(
                    Entity.xml("my-new-start-group-name"));

            Assert.assertEquals(200, response.getStatus());
        }

        {
            final WebTarget manager = target(String.format(
                    "competition/%s/group/%s#name",
                    competitionId, startGroupId));

            final Response response = manager.request().get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("my-new-start-group-name",
                    response.readEntity(String.class));
        }

    }
}
