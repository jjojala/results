/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.gemini.results.model.Competition;


@Path("competition")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitionResource {

    private Competition competition;

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        return Response.ok(this.competition).build();
    }

    @POST
    public Response create(@Context final UriInfo ui,
            final Competition competition) {
        if (competition.getId() == null ||
                competition.getId().isEmpty())
            competition.setId(UUID.randomUUID().toString());

        this.competition = competition;

        return Response.created(UriBuilder.fromUri(ui.getRequestUri())
                .build(competition.getId())).build();
    }
}
