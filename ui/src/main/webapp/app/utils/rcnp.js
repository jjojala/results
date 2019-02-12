/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */

define(function() {
    'use strict';

    angular.module('utils').service('Rcnp',
        function(Uuid, $websocket) {

            var wsUrl = 
                    (window.location.protocol === 'https:' ? 'wss://' : 'ws://') +
                    window.location.host + '/notifications/' + Uuid.randomUUID();

            var ws = $websocket.$new(wsUrl, [ 'x-rcnp' ]);
            ws.$on('$open', function() {
                console.log('Rcnp: WebSocket opened for ' + wsUrl);
            });

            ws.$on('$close', function() {
                console.log('Rcnp: WebSocket closed.');
            });

            var handlers = [];

            ws.$on('$message', function(msg) {
                if (msg.event) {
                    var parts = msg.event.split(/\s+/);

                    for (var i = 0; i < handlers.length; i++) {
                        if ((!handlers[i].event || handlers[i].event === parts[0]) &&
                                (!handlers[i].entityClass || handlers[i].entityClass === parts[1]))
                            handlers[i].handler(msg.data, parts[2], parts[0], parts[1]);
                    }
                }
            });

            ws.$on('$error', function(err) {
                console.log('onError: ' + err);
            });

            return {
                /**
                 * @name Register a callback function for RCNP notifications
                 * @param {function({string} data, {String} eventName, {String} entityType)} handler-function
                 * @param {String} name of the event for which this registration is made for
                 * @param {String} entity type being subject of the event for which this registration is made for 
                 * @returns {undefined}
                 */
                register: function(f, e, c) {
                    handlers.push({ handler: f, event: e, entityClass: c });
                }
            };
        });

});