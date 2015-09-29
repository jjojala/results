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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Competition;
import org.gemini.results.model.Group;
import org.gemini.results.model.GroupList;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class GroupResource {

    private static final Logger LOG = Logger.getLogger(
            GroupResource.class.getName());

    private final EntityManagerFactory emf_;
    private final ResourceListener listener_;
    private final String competitionId_;

    public GroupResource(final EntityManagerFactory emf,
            final ResourceListener listener,
            final String competitionId) {
        emf_ = emf;
        listener_ = listener;
        competitionId_ = competitionId;
    }

    @GET
    public Response list() {
        final EntityManager em = emf_.createEntityManager();

        try {
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            final List<Group> groups = em.createNamedQuery("Group.list")
                    .setParameter(1, competitionId_).getResultList();

            return RestUtils.ok(new GroupList(groups));
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group list failed: ", ex);
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
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            final Group group = em.find(Group.class, id);
            if (group != null)
                return Response.ok(group).build();

            return RestUtils.notFound(Group.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group get failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
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

            if (DataUtils.findWithLock(
                    em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            group.setId(id);
            group.setCompetitionId(competitionId_);
            DataUtils.create(em, id, group);
            trx.commit();

            listener_.onCreate(Group.class, group);
            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict(Group.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group create failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Group group) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();

            if (DataUtils.findWithLock(
                    em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            group.setId(id);
            group.setCompetitionId(competitionId_);

            DataUtils.update(em, id, group);

            trx.commit();

            listener_.onUpdate(Group.class, group);
            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound(Group.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group update failed: ", ex);
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
            if (DataUtils.find(em, Competition.class, competitionId_) == null)
                return RestUtils.notFound(Competition.class, competitionId_);

            trx.begin();
            final Group group = DataUtils.findWithLock(em, Group.class, id);
            if (group == null)
                return RestUtils.notFound(Group.class, id);

            DataUtils.remove(em, id, Group.class); // == true

            trx.commit();

            listener_.onRemove(Group.class, group);
            return Response.ok().build();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group remove failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Group group = DataUtils.find(em, Group.class, id);
            if (group == null)
                return RestUtils.notFound(Group.class, id);

            return RestUtils.ok(group.getName());
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group getName failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Group group = DataUtils.findWithLock(em, Group.class, id);
            if (group == null)
                return RestUtils.notFound(Group.class, id);

            group.setName(name);
            DataUtils.update(em, id, group);

            trx.commit();

            listener_.onUpdate(Group.class, group);
            return Response.ok().build();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Group setName failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }
}
