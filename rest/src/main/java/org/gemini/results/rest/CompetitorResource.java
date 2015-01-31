/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.gemini.results.model.Competitor;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitorResource {

    private final EntityManagerFactory emf_;
    private final String competitionId_;

    public CompetitorResource(final EntityManagerFactory emf,
            final String competitionId) {
        emf_ = emf;
        competitionId_ = competitionId;
    }

    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id, final Competitor clazz) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") final String id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}#name")
    public Response setName(@PathParam("id") final String id,
            final String name) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{id}#offset")
    public Response getOffset(@PathParam("id") final String id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}#offset")
    public Response setOffset(@PathParam("id") final String id,
            final Long offset) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
