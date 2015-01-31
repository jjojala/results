/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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
import org.gemini.results.model.Group;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class GroupResource {

    private final EntityManagerFactory emf_;
    private final String competitionId_;

    public GroupResource(final EntityManagerFactory emf,
            final String competitionId) {
        emf_ = emf;
        competitionId_ = competitionId;
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();

        try {
            final Group group = em.find(Group.class, id);
            if (group != null)
                return Response.ok(group).build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id,
            final Group group) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            group.setId(id);
            em.persist(group);
            trx.commit();

            /*
            if (getNoLock(id) != null)
                return Response.status(Response.Status.CONFLICT).build();
            */

            return Response.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build()).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Group group = em.find(Group.class, id);
            em.remove(group);
            trx.commit();

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();

        try {
            final Group group = em.find(Group.class, id);
            if (group == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(group.getName()).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @PUT
    @Path("{id}#name")
    public Response setName(@PathParam("id") final String id,
            final String name) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Group group = em.find(Group.class, id);
            group.setName(name);
            em.merge(group);
            trx.commit();

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }
}
