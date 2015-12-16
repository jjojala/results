/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */

'use strict';

angular.module('utils', []).service('Uuid',
    function() {
        return {
            randomUUID: function() {
                return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
                        .replace(/[xy]/g, function(c) {
                    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                    return v.toString(16);
                });            
            }
        };
    }
);

