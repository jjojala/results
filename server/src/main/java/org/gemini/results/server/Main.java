/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.server;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.gemini.results.rest.ArchiveResource;
import org.gemini.results.rest.CompetitionResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {

    public static void main(final String[] args) {
        try {
            final URI restUri = UriBuilder.fromUri(
                    "http://0.0.0.0:8800/rest/").build();

            final ResourceConfig config = new ResourceConfig()
                    .register(CompetitionResource.class)
                    .register(ArchiveResource.class)
                    .register(MoxyJsonFeature.class);

            final HttpServer server =
                    GrizzlyHttpServerFactory.createHttpServer(restUri, config);

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
