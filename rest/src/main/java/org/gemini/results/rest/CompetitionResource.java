/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
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
import javax.xml.datatype.XMLGregorianCalendar;
import org.gemini.results.model.Competition;

@Singleton
@Path("competition")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitionResource {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("results-data");

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        final EntityManager em = emf.createEntityManager();
        try {
            final Competition competition = em.find(Competition.class, id);
            if (competition != null)
                return Response.ok(competition).build();

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Competition competition) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            em.merge(competition);
            trx.commit();

            /*
            if (!competitions.containsKey(id))
                return Response.status(Response.Status.NOT_FOUND).build();
            */

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context final UriInfo ui, 
            @PathParam("id") final String id,
            final Competition competition) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            competition.setId(id);
            em.persist(competition);
            trx.commit();

            /*
            if (competitions.containsKey(id))
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
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c = em.find(Competition.class, id);
            em.remove(c);
            trx.commit();

            return Response.ok().build();

            /*
            if (competitions.remove(id) != null)
                return Response.ok().build();

            return Response.status(Response.Status.NOT_FOUND).build();
            */
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        final EntityManager em = emf.createEntityManager();

        try {
            final Competition c = em.find(Competition.class, id);
            return Response.ok(c.getName()).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @PUT
    @Path("{id}#name")
    public Response setName(@PathParam("id") final String id,
            final String name) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c = em.find(Competition.class, id);
            c.setName(name);
            em.merge(c);
            trx.commit();

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @GET
    @Path("{id}#time")
    public Response getTime(@PathParam("id") final String id) {
        final EntityManager em = emf.createEntityManager();

        try {
            final Competition c = em.find(Competition.class, id);
            return Response.ok(c.getTime()).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @PUT
    @Path("{id}#time")
    public Response setTime(@PathParam("id") final String id,
            final XMLGregorianCalendar time) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c = em.find(Competition.class, id);
            c.setTime(time);
            trx.commit();

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @GET
    @Path("{id}#organizer")
    public Response getOrganizer(@PathParam("id") final String id) {
        final EntityManager em = emf.createEntityManager();

        try {
            final Competition c = em.find(Competition.class, id);
            return Response.ok(c.getOrganizer()).build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @PUT
    @Path("{id}#organizer")
    public Response setOrganizer(@PathParam("id") final String id,
            final String organizer) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c = em.find(Competition.class, id);
            c.setOrganizer(organizer);
            em.merge(c);

            return Response.ok().build();
        }

        finally {
            try { em.close(); } catch (final Throwable ignored) {}
        }
    }

    @Path("{id}/group")
    public GroupResource getGroupResource(
            @PathParam("id") final String id) {
        return new GroupResource(emf, id);
    }

    @Path("{id}/class")
    public ClazzResource getClazzResource(
            @PathParam("id") final String id) {
        return null;
    }
}
