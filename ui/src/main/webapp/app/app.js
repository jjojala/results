/*
 * Copyright (C) 2015-2016 Jari Ojala (jari.ojala@iki.fi).
 */

requirejs.config({
    baseUrl: '../',
    paths: {
        'angular' : 'lib/angular/1.5.0-beta.2/angular.min',
        'angular-route' : 'lib/angular-route/1.5.0-beta.2/angular-route.min',
        'ui-bootstrap' : 'lib/angular-bootstrap/0.14.3/ui-bootstrap.min',
        'ui-bootstrap-tpls' : 'lib/angular-bootstrap/0.14.3/ui-bootstrap-tpls.min',
        'ng-websocket' : 'lib/ng-websocket/0.2.1/ng-websocket'
    }
});

requirejs([
    'angular',
    'angular-route',
    'ui-bootstrap',
    'ui-bootstrap-tpls',
    'ng-websocket',
    
    'app/utils/utils.module',
    'app/utils/uuid',
    'app/utils/time-editor',
    'app/utils/rcnp',
    'app/utils/filters.js',
    'app/model/model.module',
    'app/model/model',
    'app/components/competition.module',
    'app/components/competition-list/competition-list',
    'app/components/competition-main/competition-main',
    'app/utils/utils.module',
    'app/app.routes'
], function() {
'use strict';

var app = angular.module('app', [ 'utils',
    'ngRoute', 'ui.bootstrap', 'ngWebsocket', 'app.routes' ]);

    angular.bootstrap(document, ['app']);
});