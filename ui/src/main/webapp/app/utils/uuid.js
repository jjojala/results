/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */

(function() {
    'use strict';

    /**
     * @memberOf utils
     * @name Uuid
     * @desc Utility for UUID-related operations.
     */
    function uuidService() {

        return {
            /**
             * @description Get UUID alike unique string.
             * @memberOf Uuid
             * @function randomUUID
             * @returns {String} UUID a-alike unique string.
             */
            randomUUID: function() {
                return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
                        .replace(/[xy]/g, function (c) {
                        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                                return v.toString(16);
                        });
            }
        };
    }

    angular.module('utils').service('Uuid', uuidService);
})();

