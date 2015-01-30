/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.concurrent.locks.ReadWriteLock;
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
import org.gemini.results.model.Group;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class StartGroupResource {

    private final ReadWriteLock lock_;
    private final Competition competition_;

    public StartGroupResource(final ReadWriteLock lock,
            final Competition competition) {
        lock_ = lock;
        competition_ = competition;
    }

    private Group getNoLock(final String id) {
        for (final Group group: competition_.getGroups())
            if (group.getId().equals(id))
                return group;

        System.out.println("StartGroup " + id + " not found!");
        return null;
    }

    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id,
            final Group group) {
        try {
            lock_.writeLock().lock();
            if (getNoLock(id) != null)
                return Response.status(Response.Status.CONFLICT).build();

            group.setId(id);
            competition_.getGroups().add(group);
            return Response.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build()).build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();
            final Group group = getNoLock(id);
            if (group != null)
                return Response.ok(group).build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") final String id) {
        try {
            lock_.writeLock().lock();
            final Group group = getNoLock(id);
            if (group == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            competition_.getGroups().remove(group);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();
            final Group group = getNoLock(id);
            if (group == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(group.getName()).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @PUT
    @Path("{id}#name")
    public Response setName(@PathParam("id") final String id,
            final String name) {
        try {
            lock_.writeLock().lock();
            final Group group = getNoLock(id);
            if (group == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            group.setName(name);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }
}
