/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
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
import javax.xml.datatype.XMLGregorianCalendar;
import org.gemini.results.data.DataUtils;
import org.gemini.results.model.Clazz;
import org.gemini.results.model.Event;
import org.gemini.results.model.EventList;
import org.gemini.results.model.Competitor;
import org.gemini.results.model.Group;

@Singleton
@Path("events")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class EventResource {

    private static final Logger LOG = Logger.getLogger(EventResource.class.getName());
    
    private final EntityManagerFactory emf_;
    private final ResourceListener listener_;

    public EventResource(final EntityManagerFactory emf,
            ResourceListener listener) {
        emf_ = emf;
        listener_ = new ResourceListenerWrapper(listener);
    }

    @GET
    public Response list() {
        final EntityManager em = emf_.createEntityManager();

        try {
            final List<Event> events =
                    em.createNamedQuery("Event.list").getResultList();

            return RestUtils.ok(new EventList(events));
        }
        
        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "'Event.list' failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @GET
    @Path("export/{id}")
    public Response export(@PathParam("id") final String id) {
        final EntityManager em = emf_.createEntityManager();
        try {
            final Event event =
                    DataUtils.find(em, Event.class, id);
            if (event == null)
                return RestUtils.notFound(Event.class, id);

            event.getGroups().addAll(em.createNamedQuery("Group.list")
                    .setParameter(1, id).getResultList());
            event.getClasses().addAll(em.createNamedQuery("Clazz.list")
                    .setParameter(1, id).getResultList());
            event.getCompetitors().addAll(em.createNamedQuery(
                    "Competitor.list").setParameter(1, id).getResultList());

            return RestUtils.ok(event);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition export failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @POST
    @Path("import/{id}")
    public Response _import(@Context final UriInfo ui,
            @PathParam("id") final String id,
            final Event competition) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            System.out.println("Importing...");
            competition.setId(id);
            DataUtils.create(em, id, competition);

            for (final Group group: competition.getGroups()) {
                System.out.println("group: " + group.getId());
                group.setEventId(id);
                DataUtils.create(em, group.getId(), group);
            }

            for (final Clazz clazz: competition.getClasses()) {
                clazz.setEventId(id);
                DataUtils.create(em, clazz.getId(), clazz);
            }

            for (final Competitor competitor: competition.getCompetitors()) {
                competitor.setEventId(id);
                DataUtils.create(em, competitor.getId(), competitor);
            }

            trx.commit();

            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict(ex);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition import failed: ", ex);
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
            final Event competition =
                    DataUtils.find(em, Event.class, id);
            if (competition == null)
                return RestUtils.notFound(Event.class, id);

            return RestUtils.ok(competition);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition get failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id,
            final Event competition) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            competition.setId(id);
            DataUtils.update(em, id, competition);
            trx.commit();

            listener_.onUpdate(Event.class, competition, id);
            
            return RestUtils.ok();
        }

        catch (final EntityNotFoundException ex) {
            return RestUtils.notFound(Event.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition update failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @POST
    @Path("{id}")
    public Response create(@Context final UriInfo ui, 
            @PathParam("id") final String id,
            final Event competition) {
        final EntityManager em = emf_.createEntityManager();
        final EntityTransaction trx = em.getTransaction();

        try {
            trx.begin();
            competition.setId(id);
            DataUtils.create(em, id, competition);
            trx.commit();

            listener_.onCreate(Event.class, competition, id);

            return RestUtils.created(UriBuilder.fromUri(
                    ui.getRequestUri()).build());
        }

        catch (final EntityExistsException ex) {
            return RestUtils.conflict(Event.class, id);
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition create failed: ", ex);
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
            final Event competition =
                    DataUtils.findWithLock(em, Event.class, id);
            if (competition == null)
                return RestUtils.notFound(Event.class, id);
            
            DataUtils.remove(em, id, Event.class); //  == true
            trx.commit();

            listener_.onRemove(Event.class, competition, id);

            return RestUtils.ok();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition remove failed: ", ex);
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
            final Event c = DataUtils.find(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            return RestUtils.ok(c.getName());
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition getName failed: ", ex);
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
            final Event c =
                    DataUtils.findWithLock(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            c.setName(name);
            em.merge(c);
            trx.commit();
            
            listener_.onUpdate(Event.class, c, c.getId());
            return RestUtils.ok();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition setName failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Event c = DataUtils.find(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            return RestUtils.ok(c.getTime());
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition getTime failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Event c =
                    DataUtils.findWithLock(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            c.setTime(time);
            em.merge(c);
            trx.commit();
            
            listener_.onUpdate(Event.class, c, c.getId());
            return RestUtils.ok();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition setTime failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Event c = DataUtils.find(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            return RestUtils.ok(c.getOrganizer());
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition getOrganizer failed: ", ex);
            return Response.serverError().entity(ex).build();
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
            final Event c =
                    DataUtils.findWithLock(em, Event.class, id);
            if (c == null)
                return RestUtils.notFound(Event.class, id);

            c.setOrganizer(organizer);
            em.merge(c);
            trx.commit();

            listener_.onUpdate(Competitor.class, c, c.getId());
            return RestUtils.ok();
        }

        catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Competition setOrganizer failed: ", ex);
            return Response.serverError().entity(ex).build();
        }

        finally {
            DataUtils.close(em);
        }
    }

    @Path("{id}/groups")
    public GroupResource getGroupResource(
            @PathParam("id") final String id) {
        return new GroupResource(emf_, listener_, id);
    }

    @Path("{id}/classes")
    public ClazzResource getClazzResource(
            @PathParam("id") final String id) {
        return new ClazzResource(emf_, listener_, id);
    }

    @Path("{id}/competitors")
    public CompetitorResource getCompetitorResource(
            @PathParam("id") final String id) {
        return new CompetitorResource(emf_, listener_, id);
    }
}
