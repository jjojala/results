/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
import javax.inject.Singleton;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
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
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Competition;
import org.gemini.results.model.CompetitionList;

@Singleton
@Path("competition")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitionResource {

    private final EntityManagerFactory emf_;

    public CompetitionResource(final EntityManagerFactory emf) {
        emf_ = emf;
    }

    @GET
    public Response list() {
        final EntityManager em = emf_.createEntityManager();

        try {
            final List<Competition> competitions =
                    em.createNamedQuery("Competition.list").getResultList();

            return RestUtils.ok(new CompetitionList(competitions));
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();
        try {
            final Competition competition =
                    DataUtils.find(em, Competition.class, id);
            if (competition == null)
                return RestUtils.notFound(
                        DataUtils.makeMessage(Competition.class, id));

            return RestUtils.ok(competition);
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Competition competition) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            competition.setId(id);
            DataUtils.update(em, id, competition);
            trx.commit();

            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound(
                    "EntityNotFoundException: " + ex.getMessage());
        }

        finally {
            DataUtils.close(em);
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context final UriInfo ui, 
            @PathParam("id") final String id,
            final Competition competition) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            competition.setId(id);
            DataUtils.create(em, id, competition);
            trx.commit();

            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            DataUtils.remove(em, id, Competition.class);
            trx.commit();

            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound("EntityNotFoundException: "
                    + ex.getMessage());
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();

        try {
            final Competition c = DataUtils.find(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(DataUtils.makeMessage(
                        Competition.class, id));

            return RestUtils.ok(c.getName());
        }

        finally {
            DataUtils.close(em);
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
            final Competition c =
                    DataUtils.findWithLock(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(
                        DataUtils.makeMessage(Competition.class, id));

            c.setName(name);
            em.merge(c);
            trx.commit();

            return RestUtils.ok();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("{id}#time")
    public Response getTime(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();

        try {
            final Competition c = DataUtils.find(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(DataUtils.makeMessage(
                        Competition.class, id));

            return RestUtils.ok(c.getTime());
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}#time")
    public Response setTime(@PathParam("id") final String id,
            final XMLGregorianCalendar time) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c =
                    DataUtils.findWithLock(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(
                        DataUtils.makeMessage(Competition.class, id));

            c.setTime(time);
            trx.commit();

            return RestUtils.ok();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("{id}#organizer")
    public Response getOrganizer(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();

        try {
            final Competition c = DataUtils.find(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(DataUtils.makeMessage(
                        Competition.class, id));

            return RestUtils.ok(c.getOrganizer());
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}#organizer")
    public Response setOrganizer(@PathParam("id") final String id,
            final String organizer) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            final Competition c =
                    DataUtils.findWithLock(em, Competition.class, id);
            if (c == null)
                return RestUtils.notFound(
                        DataUtils.makeMessage(Competition.class, id));

            c.setOrganizer(organizer);
            em.merge(c);

            return RestUtils.ok();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @Path("{id}/group")
    public GroupResource getGroupResource(
            @PathParam("id") final String id) {
        return new GroupResource(emf_, id);
    }

    @Path("{id}/class")
    public ClazzResource getClazzResource(
            @PathParam("id") final String id) {
        return new ClazzResource(emf_, id);
    }

    @Path("{id}/competitor")
    public CompetitorResource getCompetitorResource(
            @PathParam("id") final String id) {
        return new CompetitorResource(emf_, id);
    }
}
