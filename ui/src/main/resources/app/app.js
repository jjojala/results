/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', [ 
    'ngRoute' /*, 'ui.bootstrap' */]);

app.config(['$routeProvider', function($routeProvider) {
   $routeProvider
           .when('/competition-list', {
                    templateUrl: '/view/competition-list-tmpl.html',
                    controller: 'CompetitionListController'
                })
           .when('/competition/:competitionId', {
                    templateUrl: '/view/competition-main-tmpl.html',
                    controller: 'CompetitionMainController'
                })
           .otherwise({
                    redirectTo: '/competition-list'
                })
}]);

app.service('Uuid', function() {
    return {
        randomUUID: function() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
                    .replace(/[xy]/g, function(c) {
                var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                return v.toString(16);
            });            
        }
    };
});

app.controller('CompetitorsController',
    function($scope, $http) {
        console.log('competition: ' + angular.toJson($scope.competition, true));
        $scope.competitors = [
            { name: 'Ojala Jari' },
            { name: 'Joenperä Lenita' },
            { name: 'Ojala Jenny' },
            { name: 'Ojala Kalle' },
            { name: 'Ojala Aleksi' }
        ];
        /*
        $http.get("rest/competitor/?classId=" + $scope.competition.id)
            .success(function (data) {
                $scope.competitors = data;
            })
            .error(function (err, status) {
                alert(err + ' ' + status);
            });
            */
    });

app.controller('CompetitionMainController',
    function($scope, $http, $routeParams, Uuid) {
        $http.get("rest/competition/" + $routeParams.competitionId)
            .success(function (data) {
                $scope.competition = data;
            })
            .error(function (err, status) {
                alert(err + ' ' + status);
            });
        
        $scope.competitionId = Uuid.randomUUID();
    });

app.controller('CompetitionListController', function ($scope, $http, Uuid) {

    $scope.current = {};

    $http.get("rest/competition").success(function (data) {
        for (i = 0; i < data.length; ++i)
            data[i].timeObject = new Date(data[i].time);

        $scope.competitions = data;
    }).error(function (err, status) {
        alert(status);
    });

    $scope.onSelect = function(c) {
        $scope.current = {
            id: c.id,
            time: new Date(c.time),
            name: c.name,
            organizer: c.organizer
        };
    }

    $scope.onCreate = function(c) {
        c.id = Uuid.randomUUID();
        alert('TODO: Create: ' + angular.toJson(c, true));
        $http.post("rest/competition/" + c.id, c).success(function() {
            $scope.competitions.push(c);
        }).error(function (err, status) {
            alert("Adding competition failed: \nerr: " + err + "\nstatus: "
                    + status + "\nCompetition:"+ angular.toJson(c, true));
        });
    }
    
    $scope.onSave = function(c) {
        delete c.timeObject;
        alert('TODO: Updating: ' + angular.toJson(c, true));
        $http.put("rest/competition/" + c.id, c).success(function() {
            console.log("Great, succeeded!");
        }).error(function (err, status) {
            alert("Updating competition failed: \nerr: " + err + "\nstatus: "
                    + status + "\nCompetition:"+ angular.toJson(c, true));
        });
    }

    $scope.onDestroy = function(c, i) {
        alert('TODO: Destroy: ' + angular.toJson(c, true));
        $http.delete("rest/competition/" + c.id).success(function () {
            $scope.competitions.splice(i, 1);
        }).error(function (err) {
            alert("Deleting competition failed: " + err.statusText);
        });
    }

    $scope.onDownload = function(c) {
        alert('TODO: Download: ' + angular.toJson(c, true));
        $http.get("rest/competition/export/" + c.id);
    }

    $scope.sortCriteria = 'time';
});