/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.server;

import java.util.Iterator;
import java.util.Set;
import org.gemini.results.rest.ResourceListener;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

public class NotificationService extends WebSocketApplication
{

    public ResourceListener getResourceListener() {
        return new ResourceListener() {

            @Override
            public void onCreate(Class<?> resourceType, Object resultingResource) {
                broadcast(getWebSockets(), resultingResource);
            }

            @Override
            public void onUpdate(Class<?> resourceType, Object resultingResource) {
                broadcast(getWebSockets(), resultingResource);
            }

            @Override
            public void onRemove(Class<?> resourceType, Object removedResource) {
                broadcast(getWebSockets(), removedResource);
            }
        };
    }
    
    private static void broadcast(final Set<WebSocket> sockets, final Object msg) {
        final Iterator<WebSocket> it = sockets.iterator();
        if (it.hasNext())
            it.next().broadcast(sockets, msg.toString());
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
