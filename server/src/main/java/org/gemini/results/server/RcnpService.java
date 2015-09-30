/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.gemini.results.rest.ResourceListener;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 * Resource Change Notification Protocol (RCNP) broker service.
 * 
 * <p>RCNP is a WebSocket -based protocol for sending <i>resource
 * change</i> notifications for subscribed clients. The protocol
 * implies fundamentally two roles: <i>client</i> and the <i>broker</i>.
 * A <i>client</i> can additionally act in two flavor, as
 * as <i>subscriber</i> or as a <i>producer</i>. This class implements
 * the <i>broker</i>.
 * 
 * <h3>Establishing a connection</h3>
 * 
 * <p>For establishing a connection, a <i>client</i> will create
 * a WebSocket session with the <i>broker</i> by using the WebSocket
 * endpoint as served by the <i>broker</i>. The supported subprotocol
 * is {@code x-rcnp}, which MUST be used by the <i>client</i>.
 *  
 * <p>As part of the connection URL, the <i>client</i> may use the following
 * query parameters:
 * <ul>
 *   <li>{@code clientId}></li>
 *   <li>{@code contentType}</li>
 *   <li>{@code filter}</li>
 * </ul>
 * 
 * <p>{@code clientId} is unique identifier of the client. 
 * MAY pass a unique {@code clientId}. If not given,
 * the broker MUST assign a unique {@code clientId} for that particular
 * <i>client</i>.
 * 
 * <p>In addition, the <i>client</i> MAY supply a {@code contentType}, which
 * is the MIME type of the desired notifications. If not given, the 
 * <i>broker</i> will use the default {@code contentType}.
 * 
 * <p>In order to subscribe notifications, the <i>client</i> MUST pass
 * a {@code filter}, which is an URL encoded LDAP filter string. If not
 * given, the <i>broker</i> MUST not submit any notifications for that
 * particular <i>client. If given, the <i>broker</i> must match a
 * notification submitted for it with the <i>client</i> specific
 * {@code filter}, and in case of match, the <i>broker</i> MUST
 * submit the notification also for that particular <i>client</i>.
 * 
 * <p>An xample:
 * 
 * <p>{@code ws://localhost:8080/notifications/?clientId=abc&accept=application/json&filter=(!(originId=abc))}
 * specifies the connection, where the <i>client</i> will be associated with a
 * {@code clientId} {@code 'abc'}, the notifications will be passed as
 * JSON, and all notifications but the ones with attribute {@code originId=abc}
 * will be subscribed.
 * 
 * <h3>Enquiring the subscription</h3>
 * 
 * <p>Once, connected the <i>client</i> may, at any time, submit an subscription
 * enquery, for which the <i>broker</i> MUST reply as soon as possible. The
 * response MUST be submitted regardless whether it would be otherwise blocked
 * by the {@code filter}. The enquery, nor the response MUST not be submitted
 * to any other <i>client</i>, except the one that made the request.
 * 
 * <p>The request (as JSON):
 * <pre><tt>
 * {
 *   messageType: 'request',
 *   request: {
 *     method: 'GET',
 *     uri: '/subscription',
 *     headers: [
 *        'Accept': 'application/json'
 *     ]
 *   }
 * }
 * </tt></pre>
 * 
 * <p>, and the response (as JSON):
 * <pre><tt>
 *  {
 *    messageType: 'response',
 *    response: {
 *       status: 'HTTP 200 OK'
 *       headers: [
 *          'Content-Type': 'application/json',
 *       ],
 *       data: {
 *          'clientId': 'abc',
 *          'contentType': 'application/json',
 *          'filter': '(!(originId=abc))'
 *       }
 *    }
 *  }
 * </tt></pre>
 * 
 * <h3>Updating the subscption</h3>
 * 
 * <p>The <i>client</i> may also, at any time change the subscription. However
 * the {@code clientId} cannot be changed after connection establishment. The
 * changes will take effect as soon as possible, and as soon as possible after
 * that, the <i>broker</i> MUST submit a confirmation response.
 * 
 * <p>The request (as JSON):
 * <pre><tt>
 * {
 *   messageType: 'request',
 *   request: {
 *     method: 'PUT',
 *     uri: '/subscription',
 *     headers: [
 *        'Accept': 'application/json',
 *        'Content-Type': 'application/json'
 *     ],
 *     data: {
 *        'contentType':'application/json',
 *        'filter': '(!(originId=abc))'
 *     }
 *   }
 * }
 * </tt></pre>
 * 
 * <p>The response (as JSON):
 * <pre><tt>
 *  {
 *    messageType: 'response',
 *    response: {
 *       status: 'HTTP 200 OK'
 *       headers: [
 *          'Content-Type': 'application/json',
 *       ],
 *       data: {
 *          'clientId': 'abc',
 *          'contentType': 'application/json',
 *          'filter': '(!(originId=abc))'
 *       }
 *    }
 *  }
 * </tt></pre>
 * 
 * <h3>Disconnection</h3>
 * 
 * <p>Disconnection do not imply any additional activities on <i>client</i>
 * side beyond simply closing the WebSocket. The <i>broker</i> MUST
 * accordingly dispose the closed WebSocket as soon as possible after
 * identifying the closing.
 * 
 * <h3>Submitting notifications</h>
 * 
 * <p><i>clients</i> acting in flavor of <i>producer</i> may, at any time
 * after connection establishment, submit notifications. The notifications
 * MUST be further delivered by the <i>broker</i> to all <i>clients</i>
 * having matching {@code filter} subscribed, including also the
 * <i>client</i> that originally submitted the notification. In other words,
 * a <i>client</i> may subscribe to listen it's own notifications.
 * 
 * <p>An example:
 * <pre><tt>
 * {
 *   messageType: 'notification',
 *   headers: [
 *     'Content-Type': 'application/json'
 *   ],
 *   notification: {
 *     resourceType: 'Competitor',
 *     change: 'REMOVED',
 *     data: { ... removed competition ... }
 *   }
 * }
 * </tt></pre>
 * 
 * <h3>Receiving notifications</h3>
 * 
 * <p><i>clients</i> may subscribe to listen desired notifications
 * by specifying a {@code filter}, either when establishing a connection
 * or any time after, with the specified request. The notification
 * is fundamentally an envelope, containing information about the change
 * and finally enclosing the actual resource change, or reference to
 * change resource.
 * 
 * <p>An example:
 * <pre><tt>
 * {
 *   messageType: 'notification',
 *   originId: 'abc',   \/* MUST be set by the <i>broker</i> *\/
 *   headers: [
 *     'Content-Type': 'application/json'
 *   ],
 *   notification: {
 *     resourceType: 'Competitor',
 *     change: 'REMOVED',
 *     data: { ... removed competition ... }
 *   }
 * }
 * </tt></pre>
 */
public class RcnpService extends WebSocketApplication
{

    private static final List<String> SUPPORTED_PROTOCOLS =
            Collections.singletonList(
                    "x-rcnp" /* Resource Change Notification Protocol */);
            
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
    public List<String> getSupportedProtocols(final List<String> subProtocols) {
        final List<String> supported = new ArrayList<>();
        
        for (final String p: subProtocols) {
            if (SUPPORTED_PROTOCOLS.contains(p))
                supported.add(p);
        }
        
        return supported;
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new RcnpWebSocket(handler, requestPacket, listeners);
    }
}
