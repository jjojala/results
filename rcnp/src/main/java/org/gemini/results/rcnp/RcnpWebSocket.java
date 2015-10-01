/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rcnp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;

public class RcnpWebSocket extends DefaultWebSocket {

    private final String clientId;
    private final String contentType;
    private final String filter;

    public RcnpWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);

        final Map<String, String> parameters =
                getParameters(request.getQueryString());

        clientId = decode(parameters.get("clientId"));
        contentType = decode(parameters.get("contentType"));
        filter = decode(parameters.get("filter"));
    }

    public String getClientId() {
        return clientId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilter() {
        return filter;
    }
    
    private static Map<String, String> getParameters(final String query) {
        if (query == null || query.isEmpty())
            return Collections.emptyMap();
        
        final String[] nameValues = query.split("&");
        final Map<String, String> parameters = new HashMap<>(nameValues.length);

        for (final String nameValue: nameValues) {
            final int equalSignPos = nameValue.indexOf('=');
            if (equalSignPos < 0) {
                /* No equal sign: "noEqualSign&equalSign=..." */
                parameters.put(nameValue, null);
            } else if (equalSignPos > 0) {
                /* Equal sign, and name's not empty: "i=..." */
                final String name = nameValue.substring(0, equalSignPos);
                if (equalSignPos < (nameValue.length()-1)) {
                    /* non-empty value: "i=a..." */
                    parameters.put(name,
                            nameValue.substring(equalSignPos+1));
                } else {
                    /* no value: "i=&i2=" */
                    parameters.put(name, null);
                }
            }
        }

        return parameters;
    }

    private static final String decode(final String str) {
        try {
            if (str == null || str.isEmpty())
                return str;

            return URLDecoder.decode(str, "UTF-8");
        }

        catch (final UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }
}
