/*
 * Copyright (C) 2015-2016 Jari Ojala (jari.ojala@iki.fi).
 */

define(function() {
'use strict';

angular.module('app.competition')
    .controller('CompetitionListController', ['$scope', '$http',
        'Uuid', 'Rcnp', CompetitionListController]);

    function CompetitionListController($scope, $http, Uuid, Rcnp) {

        $scope.current = null;
        $scope.competitions = [];
        $scope.competitionDetailsShow = false;
        $scope.eventImportDialogVisible = false;
        
        $scope.showEventImportDialog = function() {
            $scope.eventImportDialogVisible = true;
        };
        
        $scope.hideEventImportDialog = function() {
            $scope.eventImportDialogVisible = false;
        };
        
        $scope.openCompetitionDetails = function (c, i) {
            $scope.competitionDetailsShow = true;
        };
        
        $scope.hideCompetitionDetails = function() {
            $scope.current = null;
            $scope.competitionDetailsShow = false;
        };
        
        Rcnp.register(function(c) {
                $scope.$apply(function() {
                    $scope.competitions.push(c);
                });
            },
            'CREATED', 'org.gemini.results.model.Event');

        Rcnp.register(function(c) {
            $scope.$apply(function() {
                for (var i = 0; i < $scope.competitions.length; i++) {
                    if ($scope.competitions[i].id == c.id) {
                        $scope.competitions[i] = c;
                        break;
                    }
                }
            });
        }, 'UPDATED', 'org.gemini.results.model.Event');

        Rcnp.register(function(c) {
            $scope.$apply(function() {
                for (var i = 0; i < $scope.competitions.length; i++) {
                    if ($scope.competitions[i].id === c.id) {
                        $scope.competitions.splice(i, 1);
                        break;
                    }
                }
            });
        }, 'REMOVED', 'org.gemini.results.model.Event');
        
        $http.get("rest/events").success(function (data) {
            for (var i = 0; i < data.length; ++i)
                data[i].timeObject = new Date(data[i].time);

            $scope.competitions = data;
        }).error(function (err, status) {
            alert('Retrieving events failed: \n' + 'err: ' + err + '\n' +
                    'status: ' + status);
        });

        $scope.onSelect = function(c) {
            $scope.current = {
                id: c.id,
                time: new Date(c.time),
                name: c.name,
                organizer: c.organizer
            };
        };

        $scope.onCreate = function(c) {
            c.id = Uuid.randomUUID();
            $http.post("rest/events/" + c.id, c)
                .success(function() { $scope.current = {}; })
                .error(function(err, status) {
                    alert('Adding event failed: \n' + 'err: '  + err + '\n' +
                        'status: ' + status + '\n' + 'event: ' +
                        angular.toJson(c, true));
                    });
            };

        $scope.onSave = function(c) {
            delete c.timeObject;
            $http.put("rest/events/" + c.id, c)
                .success(function() { $scope.current = {}; })
                .error(function (err, status) {
                    alert('Updating event failed: \n' + 'err: ' + err + '\n' +
                        'status: ' + status + '\n' + 'event: ' +
                        angular.toJson(c, true));
                    });
            };

        $scope.onDestroy = function(c, i) {
            $http.delete("rest/events/" + c.id)
                .error(function (err, status) {
                    alert('Deleting event failed: \n' + 'err: ' + err + '\n' +
                        'status: ' + status + '\n' + 'event: ' +
                        angular.toJson(c, true));
                });
        };

        $scope.onDownload = function(c) {
            $http.get("rest/events/export/" + c.id);
        };

        $scope.sortCriteria = 'time';
    }
});