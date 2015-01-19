/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.gemini.results.model.Competition;
import org.gemini.results.model.ModelUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class ArchiveResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(ArchiveResource.class);
    }

    @Test
    public void testCreate() {
        {
            final Competition competition = new Competition("my-id-overriden",
                    ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        "2015-01-19T22:45:15.000+02:00"),
                    "my-test-competition", "my-athletic-club", null);

            final WebTarget archive = target("archive/my-id");
            final Response response = archive.request().post(
                    Entity.xml(competition));
            Assert.assertEquals(201, response.getStatus());
            Assert.assertEquals(archive.getUri().toASCIIString(),
                    response.getHeaderString("Location"));
        }

        {
            final Response response = target("archive/my-id").request().get();
            Assert.assertEquals(200, response.getStatus());
            final Competition competition =
                    response.readEntity(Competition.class);
            Assert.assertEquals("my-id", competition.getId());
            Assert.assertEquals("my-test-competition", competition.getName());
        }

        {
            final Competition competition = new Competition(null,
                    null, null, null, null);
            final WebTarget archive = target("archive/my-id");
            final Response response = archive.request().post(
                    Entity.xml(competition));
            Assert.assertEquals(409, response.getStatus());
        }
    }
}
