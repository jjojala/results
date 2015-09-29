/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.server;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

public class NotificationService extends WebSocketApplication {

    private ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(2);

    public NotificationService() {
        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                final Set<WebSocket> sockets = getWebSockets();
                if (sockets.iterator().hasNext())
                    sockets.iterator().next().broadcast(sockets,
                            Calendar.getInstance().toString());
            }
            
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        super.onClose(socket, frame);
        System.out.println("onClose(): socket=" + socket);
    }

    @Override
    public void onConnect(WebSocket socket) {
        super.onConnect(socket);
        System.out.println("onConnect(): socket=" + socket);        
    }

    @Override
    protected boolean onError(WebSocket webSocket, Throwable t) {
        System.err.print("onError(): " );
        t.printStackTrace(System.err);
        return true;
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        System.out.println("onMessage(): socket=" + socket + ", text=" + text);
    }

    @Override
    public boolean isApplicationRequest(HttpRequestPacket request) {
        System.out.println("isApplicationRequest(): request=" + request);
        return true;
    }
}
