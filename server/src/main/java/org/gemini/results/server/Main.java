/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.server;

import java.net.URI;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.UriBuilder;
import org.gemini.results.rest.CompetitionResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {

    public static void main(final String[] args) {
        try {
            final EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory("results-data");

            final URI restUri = UriBuilder.fromUri(
                    "http://0.0.0.0:8800/rest/").build();

            final ResourceConfig config = new ResourceConfig()
                    .register(new CompetitionResource(emf))
                    .register(MoxyJsonFeature.class);

            final HttpServer server =
                    GrizzlyHttpServerFactory.createHttpServer(restUri, config);

            // TODO: Use CLStaticHttpHandler for production
            /* like this...
            server.getServerConfiguration().addHttpHandler(
                    new CLStaticHttpHandler(
                        Main.class.getClassLoader(), "/"), "/");
                    */
            server.getServerConfiguration().addHttpHandler(
                    new StaticHttpHandler(
                            "../ui/src/main/resources/",
                            "../ui/target/classes/"),
                    "/");

            server.start();

            try {
                while (true)
                    Thread.sleep(Long.MAX_VALUE);
            } catch (final InterruptedException ex) {
                System.exit(0);
            }
        }

        catch (final Throwable ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
