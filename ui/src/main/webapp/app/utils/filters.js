/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */

define(function() {
    'use strict';

    // timestamp:duration_in_msecs:format
    var timestamp = function() {
        return function(duration, format) {
            if (duration) {
                // TODO: support for format
                var base = new Date(duration).setHours(0, 0, 0, 0);
                var fractions = Math.round(duration / 100) % 10;
                var seconds = Math.floor(duration / 1000) % 60;
                var minutes = Math.floor(duration / 60000) % 60;
                var hours = Math.floor((duration - base) / 3600000);

                seconds = seconds < 10 ? '0' + seconds : String(seconds);
                minutes = minutes < 10 ? '0' + minutes : String(minutes);

                return hours + ':' + minutes + ':' + seconds + '.' + fractions;
            }
            return null;
        };
    };

    angular.module('utils').filter('timestamp', timestamp);
});

