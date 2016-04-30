/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Clazz;
import org.gemini.results.model.Event;
import org.gemini.results.model.Competitor;
import org.gemini.results.model.CompetitorList;
import org.gemini.results.model.NameList;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CompetitorResource {

    private static final Logger LOG = Logger.getLogger(
            CompetitorResource.class.getName());

    private final EntityManagerFactory emf_;
    private final ResourceListener listener_;
    private final String competitionId_;

    public CompetitorResource(final EntityManagerFactory emf,
            final ResourceListener listener,
            final String competitionId) {
        emf_ = emf;
        listener_ = listener;
        competitionId_ = competitionId;
    }

    @GET
    @Path("names")
    public Response listNames() {
        final EntityManager em = emf_.createEntityManager();

        try {
            final List<String> names = em.createNamedQuery(
                    "Competitor.listNames").getResultList();

            for (final String name: names)
                System.out.println(name);

            return RestUtils.ok(new NameList(names));
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor list failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    public Response list(@QueryParam("classId") final String classId) {
        final EntityManager em = emf_.createEntityManager();

        try {
            if (DataUtils.find(em, Event.class, competitionId_) == null)
                return RestUtils.notFound(Event.class, competitionId_);

            final List<Competitor> competitors = (classId == null)
                    ? em.createNamedQuery("Competitor.list")
                    .setParameter(1, competitionId_).getResultList()
                    : em.createNamedQuery("Competitor.listByClassId")
                    .setParameter(1, competitionId_).setParameter(2, classId)
                    .getResultList();

            return RestUtils.ok(new CompetitorList(competitors));
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor list(classId) failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            if (DataUtils.find(em, Event.class, competitionId_) == null)
                return RestUtils.notFound(Event.class, competitionId_);

            final Competitor c = DataUtils.find(em, Competitor.class, id);
            if (c == null)
                return RestUtils.notFound(Competitor.class, id);

            return RestUtils.ok(c);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor get failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }


    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id, final Competitor competitor) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            if (DataUtils.findWithLock(em, Event.class, competitionId_) == null)
                return RestUtils.notFound(Event.class, competitionId_);

            if (competitor.getClazzId() != null && DataUtils.findWithLock(
                    em, Clazz.class, competitor.getClazzId()) == null)
                return RestUtils.notFound(Clazz.class, competitor.getClazzId());

            competitor.setId(id);
            competitor.setEventId(competitionId_);
            DataUtils.create(em, id, competitor);
            trx.commit();

            listener_.onCreate(Competitor.class, competitor, id);
            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict(Competitor.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor create failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Competitor competitor) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            if (DataUtils.findWithLock(em, Event.class, competitionId_) == null)
                return RestUtils.notFound(Event.class, competitionId_);

            if (competitor.getClazzId() != null && DataUtils.findWithLock(
                    em, Clazz.class, competitor.getClazzId()) == null)
                return RestUtils.notFound(Clazz.class, competitor.getClazzId());

            competitor.setId(id);
            competitor.setEventId(competitionId_);
            DataUtils.update(em, id, competitor);

            trx.commit();

            listener_.onUpdate(Competitor.class, competitor, id);
            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound(Competitor.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor update failed: ", ex);
            return Response.serverError().entity(ex).build();
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

            if (DataUtils.findWithLock(em, Event.class, competitionId_) == null)
                return RestUtils.notFound(Event.class, competitionId_);

            final Competitor competitor = DataUtils.findWithLock(
                   em, Competitor.class, id);
            if (competitor == null)
                return RestUtils.notFound(Competitor.class, id);

            DataUtils.remove(em, id, Competitor.class); // == true
            trx.commit();

            listener_.onRemove(Competitor.class, competitor, id);
            return RestUtils.ok();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competitor remove failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
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
