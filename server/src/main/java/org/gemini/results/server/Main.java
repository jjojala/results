/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.server;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.UriBuilder;
import org.gemini.results.rest.CompetitionResource;
import org.glassfish.grizzly.filterchain.Filter;
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

public class Main {

    private final static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(final String[] args) {
        try {
            final EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory("results-data");

            final URI restUri = UriBuilder.fromUri(
                    "http://0.0.0.0:8800/rest/").build();
            
            final ResourceConfig config = new ResourceConfig()
                    .register(new CompetitionResource(emf))
                    .register(JacksonFeature.class);

            final HttpServer server =
                    GrizzlyHttpServerFactory.createHttpServer(restUri, config);
                        
            // TODO: Use CLStaticHttpHandler for production
            /* like this... */
            server.getServerConfiguration().addHttpHandler(
                    new CLStaticHttpHandler(
                        Main.class.getClassLoader(), new String[] {
                            "/META-INF/resources/webjars/"
                        }), "/lib");
                    /* */
            final StaticHttpHandler handler = new StaticHttpHandler(
                    "../ui/src/main/resources/", "../ui/target/classes/");
            handler.setFileCacheEnabled(false);
            server.getServerConfiguration().addHttpHandler(handler);


            { // Registering WebSocketAddOn for grizzly
                
                // Grizzly web-socket manual on
                // https://grizzly.java.net/websockets.html instructs
                // to register WebSocketAddOn with desired 
                // HttpServere NetworkListeners. Unfortunately that
                // doesn't seem to work if you use Jersey's
                // GrizzlyHttpServerFactory like we do. By examining
                // WebSocketAddOn's code it's possible to note that
                // the WebSocketFilter will not get registered correctly
                // to the NetworkListener's FilterChain.
                
                // Luckily, it's possible to do it directly with the
                // code above:

                for (final NetworkListener l: server.getListeners()) {
                    final FilterChain filters = l.getFilterChain();
                    for (int i = 0; i < filters.size(); ++i) {
                        if (filters.get(i) instanceof org.glassfish.grizzly.http.server.HttpServerFilter) {
                            filters.add(i, new WebSocketFilter(15*60));
                            break;
                        }
                    }
                }
            }
            
            WebSocketEngine.getEngine().register("", "/notifications",
                    new NotificationService());
            
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
