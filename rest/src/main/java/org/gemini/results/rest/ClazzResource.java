/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.concurrent.locks.ReadWriteLock;
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.datatype.Duration;
import org.gemini.results.model.Clazz;
import org.gemini.results.model.Competition;
import org.gemini.results.model.ModelUtils;
import org.gemini.results.model.StartGroup;

@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ClazzResource {

    public static final String DEFAULT_START_GROUP_ID = "DEFAULT";

    private final ReadWriteLock lock_;
    private final Competition competition_;

    public ClazzResource(final ReadWriteLock lock,
            final Competition competition) {
        lock_ = lock;
        competition_ = competition;
    }

    private Clazz getNoLock(final String id) {
        for (final StartGroup group: competition_.getStartGroups()) {
            for (final Clazz clazz: group.getClasses())
                if (clazz.getId().equals(id))
                    return clazz;
        }

        return null;
    }

    private StartGroup getStartGroupNoLock(final String id) {
        for (final StartGroup group: competition_.getStartGroups()) {
            if (group.getId().equals(id))
                return group;
        }

        return null;
    }

    private StartGroup getDefaultStartGroupNoLock() {
        final StartGroup group = getStartGroupNoLock(DEFAULT_START_GROUP_ID);
        if (group != null)
            return group;

        final StartGroup newGroup = new StartGroup(
                DEFAULT_START_GROUP_ID, null, (short)-1, (short)-1,
                ModelUtils.getDatatypeFactory().newDuration(0),
                null);

        competition_.getStartGroups().add(newGroup);
        return newGroup;
    }

    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id,
            final Clazz clazz) {
        try {
            lock_.writeLock().lock();
            if (getNoLock(id) != null)
                return Response.status(Response.Status.CONFLICT).build();

            if (clazz.getStartGroupId() != null) {
                final StartGroup group =
                        getStartGroupNoLock(clazz.getStartGroupId());
                if (group == null)
                    return Response.status(Response.Status.NOT_FOUND).build();

                group.getClasses().add(clazz);
            } else {
                final StartGroup defaultGroup = getDefaultStartGroupNoLock();
                clazz.setStartGroupId(defaultGroup.getId());
                defaultGroup.getClasses().add(clazz);
            }

            clazz.setId(id);

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
            final Clazz clazz = this.getNoLock(id);
            if (clazz != null)
                return Response.ok(clazz).build();

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
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            final StartGroup group = this.getStartGroupNoLock(
                    clazz.getStartGroupId());
            group.getClasses().remove(clazz);

            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @XmlAttribute
    private String startGroupId;
    
    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(clazz.getName()).build();
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
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            clazz.setName(name);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @GET
    @Path("{id}#offset")
    public Response getOffset(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(clazz.getOffset()).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @PUT
    @Path("{id}#offset")
    public Response setOffset(@PathParam("id") final String id,
            final Duration offset) {
        try {
            lock_.writeLock().lock();
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            clazz.setOffset(offset);
            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }

    @GET
    @Path("{id}#startGroupId")
    public Response getStartGroupId(@PathParam("id") final String id) {
        try {
            lock_.readLock().lock();
            final Clazz clazz = getNoLock(id);
            if (clazz == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(clazz.getStartGroupId()).build();
        }

        finally {
            lock_.readLock().unlock();
        }
    }

    @PUT
    @Path("{id}#startGroupId")
    public Response setStartGroupId(@PathParam("id") final String id,
            final String startGroupId) {
        try {
            lock_.writeLock().lock();

            final Clazz clazz = getNoLock(id);

            final StartGroup newGroup = getStartGroupNoLock(startGroupId);
            if (newGroup == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            if (clazz.getStartGroupId() != null)
                getStartGroupNoLock(clazz.getStartGroupId()).getClasses()
                        .remove(clazz);

            newGroup.getClasses().add(clazz);
            clazz.setStartGroupId(startGroupId);

            return Response.ok().build();
        }

        finally {
            lock_.writeLock().unlock();
        }
    }
}