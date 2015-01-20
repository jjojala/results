/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Singleton;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.gemini.results.model.Competition;


@Singleton
@Path("competition")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitionManagerResource {

    private ReadWriteLock lock_ = new ReentrantReadWriteLock();
    private Map<String, Competition> competitions = new HashMap<>();

    
    @GET
    @PathParam("id")
    public Response get(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();

            final Competition competition = competitions.get(id);
            if (competition != null)
                return Response.ok(competition).build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Competition competition) {
        try {
            lock_.writeLock().lock();

            if (!competitions.containsKey(id))
                return Response.status(Response.Status.NOT_FOUND).build();

            competitions.put(id, competition);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context final UriInfo ui,
            @PathParam("id") final String id,
            final Competition competition) {
        try {
            lock_.writeLock().unlock();

            if (competitions.containsKey(id))
                return Response.status(Response.Status.CONFLICT).build();

            competitions.put(id, competition);
            return Response.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build()).build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") final String id) {
        try {
            lock_.writeLock().lock();

            if (competitions.remove(id) != null)
                return Response.ok().build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }
}
