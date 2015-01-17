/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.ws.rs.Consumes;
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

@Path("archive")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitionArchiveResource {

    private ReadWriteLock lock_ = new ReentrantReadWriteLock();
    private Map<String, Competition> competitions_ = new HashMap<>();

    @GET
    public Response list() {
        try {
            lock_.readLock().lock();

            final List<Competition> competitions = new ArrayList<>();
            for (final Competition competition: competitions_.values()) {
                competitions.add(new Competition(competition.getId(),
                        competition.getTime(), competition.getName(),
                        competition.getOrganizer(), null));
            }

            return Response.ok(competitions).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();

            final Competition competition = competitions_.get(id);
            if (competition != null)
                return Response.ok(competition).build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context final UriInfo ui, 
            @PathParam("id") final String id,
            final Competition competition) {
        try {
            lock_.writeLock().lock();
            if (competitions_.containsKey(id))
                return Response.status(Response.Status.CONFLICT).build();

            competition.setId(id);
            competitions_.put(id, competition);

            return Response.created(UriBuilder.fromUri(
                    ui.getRequestUri()).path(id).build()).build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Competition competition) {
        try {
            lock_.writeLock().lock();
            if (!competitions_.containsKey(id))
                return Response.status(Response.Status.NOT_FOUND).build();

            competition.setId(id);
            competitions_.put(id, competition);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }
}
