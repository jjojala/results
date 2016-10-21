/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */

define(function() {
    'use strict';
        
    var Competition = function(attrs) {
        var _self = this;

        for (var a in attrs)
            this[a] = attrs[a];

        this.clone = function() {
            return new Competition(_self);
        };
    };

    var Group = function(competition, attrs) {
        var _self = this;
        var _competition = competition;

        for (var a in attrs)
            this[a] = attrs[a];

        this.competition = function() {
            return _competition;
        };

        this.clone = function() {
            return new Group(_competition, _self);
        };
    };

    var Clazz = function(group, attrs) {
        var _self = this;
        var _group = group;

        for (var a in attrs)
            this[a] = attrs[a];

        this.group = function() {
            return _group;
        };

        this.clone = function() {
            return new Clazz(_group, _self);
        };
    };

    var Competitor = function(clazz, attrs) {
        var _self = this;
        var _clazz = clazz;

        for (var a in attrs)
            this[a] = attrs[a];

        this.clazz = function() {
            return _clazz;
        };

        this.clone = function() {
            return new Competitor(_clazz, _self);
        };
    };

    angular.module('model').provider();
});

