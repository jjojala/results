/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
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
import org.gemini.results.model.ClazzList;
import org.gemini.results.model.Competition;
import org.gemini.results.model.Group;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ClazzResource {

    private final EntityManagerFactory emf_;
    private final String competitionId_;

    public ClazzResource(final EntityManagerFactory emf,
            final String competitionId) {
        emf_ = emf;
        competitionId_ = competitionId;
    }

    @GET
    public Response list(@QueryParam("groupId") final String groupId) {
        final EntityManager em = emf_.createEntityManager();

        try {
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            final List<Clazz> classes = (groupId == null || groupId.isEmpty())
                    ? em.createNamedQuery("Clazz.list")
                        .setParameter(1, competitionId_).getResultList()
                    : em.createNamedQuery("Clazz.listByGroupId")
                        .setParameter(1, competitionId_)
                        .setParameter(2, groupId).getResultList();

            return RestUtils.ok(new ClazzList(classes));
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
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            final Clazz clazz = DataUtils.find(em, Clazz.class, id);
            if (clazz != null)
                return RestUtils.ok(clazz);

            return RestUtils.notFound(Clazz.class, id);
        }

        finally {
            DataUtils.close(em);
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context UriInfo ui,
            @PathParam("id") final String id, final Clazz clazz) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();

            if (DataUtils.findWithLock(
                    em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            if (clazz.getGroupId() != null && DataUtils.findWithLock(
                    em, Group.class, clazz.getGroupId()) == null)
                return RestUtils.notFound(Group.class, clazz.getGroupId());

            clazz.setId(id);
            clazz.setCompetitionId(competitionId_);

            DataUtils.create(em, id, clazz);

            trx.commit();

            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict(Clazz.class, id);
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Clazz clazz) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();

            if (DataUtils.findWithLock(
                    em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            if (clazz.getGroupId() != null && DataUtils.findWithLock(
                    em, Group.class, clazz.getGroupId()) == null)
                return RestUtils.notFound(Group.class, clazz.getGroupId());

            clazz.setId(id);
            clazz.setCompetitionId(competitionId_);

            DataUtils.update(em, id, clazz);

            trx.commit();

            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound(Clazz.class, id);
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
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            trx.begin();
            if (DataUtils.remove(em, id, Clazz.class) == false)
                return RestUtils.notFound(Clazz.class, id);
            trx.commit();

            return RestUtils.ok();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("{id}#name")
    public Response getName(@PathParam("id") final String id) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}#name")
    public Response setName(@PathParam("id") final String id,
            final String name) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{id}#offset")
    public Response getOffset(@PathParam("id") final String id) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}#offset")
    public Response setOffset(@PathParam("id") final String id,
            final Long offset) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{id}#startGroupId")
    public Response getStartGroupId(@PathParam("id") final String id) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}#startGroupId")
    public Response setStartGroupId(@PathParam("id") final String id,
            final String startGroupId) {
        // TODO: Implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
