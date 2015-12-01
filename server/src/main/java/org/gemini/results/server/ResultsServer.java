/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.gemini.results.rcnp.RcnpService;
import java.net.URI;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.UriBuilder;
import org.gemini.results.rest.CompetitionResource;
import org.gemini.results.rest.ResourceListener;
import org.glassfish.grizzly.filterchain.FilterChain;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.grizzly.websockets.WebSocketFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ResultsServer {

    private final static String NOTIFICATIONS = "/notifications/";

    public static HttpServer createServer(final String[] args) {
        final EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("results-data");

        final URI restUri = UriBuilder.fromUri(
                "http://0.0.0.0:8800/rest/").build();

        final RcnpService notifications
                = new RcnpService(NOTIFICATIONS);

        final ResourceConfig config = new ResourceConfig()
                .register(new CompetitionResource(
                                emf, new RcnpResourceListener(notifications)))
                .register(JacksonFeature.class);

        final HttpServer server
                = GrizzlyHttpServerFactory.createHttpServer(restUri, config);

        // TODO: Use CLStaticHttpHandler for production
            /* like this... */
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(
                ResultsServer.class.getClassLoader(), new String[]{
                    "/META-INF/resources/webjars/"
                }), "/lib");
        /* */
        final StaticHttpHandler handler = new StaticHttpHandler(
                "../ui/src/main/resources/", "../ui/target/classes/");
        handler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(handler);

        {   // Registering WebSocketAddOn for grizzly

            // Grizzly web-socket manual on
            // https://grizzly.java.net/websockets.html instructs
            // to register WebSocketAddOn with desired HttpServer
            // NetworkListeners. Unfortunately that doesn't seem to work if you
            // use Jersey's GrizzlyHttpServerFactory like we do. By examining
            // WebSocketAddOn's code it's possible to note that the
            // WebSocketFilter will not get registered correctly to the
            // NetworkListener's FilterChain.
            //
            // Luckily, it's possible to do it directly with the code below:

            for (final NetworkListener l : server.getListeners()) {
                final FilterChain filters = l.getFilterChain();
                for (int i = 0; i < filters.size(); ++i) {
                    if (filters.get(i) instanceof
                            org.glassfish.grizzly.http.server.HttpServerFilter) {
                        filters.add(i, new WebSocketFilter(15 * 60));
                        break;
                    }
                }
            }
        }

        WebSocketEngine.getEngine().register("",
                notifications.getContext() + "*", notifications);

        return server;
    }
    
    public static void main(final String[] args) {
        try {
            final HttpServer server = createServer(args);
            server.start();

            try {
                while (true)
                    Thread.sleep(Long.MAX_VALUE);
            } catch (final InterruptedException ex) {
                server.shutdown();
                System.exit(0);
            }
        }

        catch (final Throwable ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
}

class RcnpResourceListener implements ResourceListener {

    private final RcnpService broker_;

    public RcnpResourceListener(final RcnpService broker) {
        this.broker_ = broker;
    }

    @Override
    public void onCreate(Class<?> resourceType, Object resultingResource,
            final Object resourceId) {
        submit("CREATED", resultingResource, resourceId);
    }

    @Override
    public void onUpdate(Class<?> resourceType, Object resultingResource,
            final Object resourceId) {
        submit("UPDATED", resultingResource, resourceId);
    }

    @Override
    public void onRemove(Class<?> resourceType, Object removedResource,
            final Object resourceId) {
        submit("REMOVED", removedResource, resourceId);
    }

    private void submit(final String eventType, final Object content,
            final Object resourceId) {
        try {
            broker_.submit(String.format("%s %s %s", eventType, 
                        content.getClass().getName(), resourceId), 
                    content);
        }
        
        catch (final JsonProcessingException ex) {
            ex.printStackTrace(System.err); // TODO: Log
        }
    }
}