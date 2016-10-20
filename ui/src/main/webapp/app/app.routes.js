/*
 * Copyright (C) 2015-2016 Jari Ojala (jari.ojala@iki.fi).
 */

(function() {
    'use strict';
    
    angular.module('app.routes', [ 'ngRoute' ]).config(['$routeProvider', config]);
    
    function config($routeProvider) {
        $routeProvider
           .when('/competition-list', {
                templateUrl: '/app/components/competition-list/competition-list-tmpl.html',
                controller: 'CompetitionListController'
            })
           .when('/competition/:competitionId', {
                templateUrl: '/app/components/competition/competition-main-tmpl.html',
                controller: 'CompetitionMainController'
            })
           .otherwise({
                redirectTo: '/competition-list'
            });
    }
    
})();