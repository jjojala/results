/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.rcnp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 * Resource Change Notification Protocol (RCNP) broker service.
 */
public class RcnpService extends WebSocketApplication {
    
    private static final Logger LOG = Logger.getLogger(
            RcnpService.class.getName());

    private static final String SUBPROTOCOL = "x-rcnp";
    private static final List<String> SUPPORTED_PROTOCOLS =
            Collections.singletonList(SUBPROTOCOL);

    private static final String EVENT = "event";
    private static final String DATA = "data";

    private final String nodeId_ = UUID.randomUUID().toString();
    private final String context_;
    private final ObjectMapper mapper_ = new ObjectMapper();
    private final JsonNodeFactory factory_ = mapper_.getNodeFactory();

    public RcnpService(final String context) {
        this.context_ = context;
    }

    public final String getContext() {
        return context_;
    }

    public void submit(final String heading, final Object resource)
        throws JsonProcessingException
    {
        final ObjectNode notification = factory_.objectNode();
        notification.set(EVENT, factory_.textNode(
                        String.format("%s %s", heading, nodeId_)));
        notification.set(DATA, mapper_.valueToTree(resource));

        final String msg = mapper_.writeValueAsString(notification);

        for (final WebSocket ws: getWebSockets())
            ws.send(msg);
    }

    @Override
    protected boolean onError(WebSocket webSocket, Throwable t) {
        System.err.print("onError(): " );
        t.printStackTrace(System.err);
        return true;
    }

    @Override
    public List<String> getSupportedProtocols(final List<String> subProtocols) {
        final List<String> supported = new ArrayList<>();
        
        for (final String p: subProtocols) {
            if (SUPPORTED_PROTOCOLS.contains(p))
                supported.add(p);
        }
        
        return supported;
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler,
            HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new RcnpWebSocket(handler, requestPacket, listeners);
    }

    private class RcnpWebSocket extends DefaultWebSocket {

        private final String nodeId_;

        public RcnpWebSocket(final ProtocolHandler protocolHandler,
                final HttpRequestPacket request,
                final WebSocketListener... listeners) {
            super(protocolHandler, request, listeners);
            
            nodeId_ = RcnpService.getNodeId(
                    request.getRequestURI(), context_.length());
        }

        public final String getNodeId() {
            return nodeId_;
        }

        @Override
        public void onMessage(final String data) {

            try {
                final ObjectNode notification =
                        (ObjectNode) mapper_.readTree(data);

                final String head = notification.get(EVENT).asText();
                
                final String[] headings = head.split(" ");
                final String changeType = headings[0];
                final String resourceClass = headings[1];
                final String resourceId = headings[2];

                final String[] visitedNodeIds =
                        Arrays.copyOfRange(headings, 3, headings.length);

                if (contains(visitedNodeIds, nodeId_)) {
                    LOG.log(Level.FINE, 
                            "Notification '{}' already visited at this node '{}'. Notification ignored.",
                            new Object[] { head, nodeId_ });
                    return;
                }

                final List<RcnpWebSocket> recipients = new ArrayList<>();
                for (final WebSocket ws: getWebSockets()) {
                    if (ws != this) {
                        final RcnpWebSocket rcnpWs = (RcnpWebSocket) ws;
                        if (!contains(visitedNodeIds, rcnpWs.getNodeId()))
                            recipients.add(rcnpWs);
                    }
                }

                if (recipients.isEmpty()) {
                    LOG.log(Level.FINE,
                            "There's no one to send notification '{}'. Notification ignored.",
                            new Object[] { head });
                    return;
                }

                notification.set(EVENT, factory_.textNode(
                        String.format("%s %s", head, nodeId_)));

                final String msg = mapper_.writeValueAsString(notification);
                
                for (final WebSocket ws: recipients)
                    ws.send(msg);
                            
                super.onMessage(data);
            }

            catch (final IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    private static boolean contains(final String[] array, final String val) {
        for (final String a: array)
            if (a.equals(val))
                return true;
        
        return false;
    }

    private static String getNodeId(final String uri, final int nodeIdOffset) {
        return uri.length() > nodeIdOffset
                ? uri.substring(nodeIdOffset) : UUID.randomUUID().toString();
    }    
}
