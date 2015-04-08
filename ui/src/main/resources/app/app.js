/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', [ 
    'ngRoute', 'datePicker', 'ui.bootstrap' ]);

app.getRandomUuid = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

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

app.controller('CompetitionMainController',
    function($scope, $http, $routeParams) {
        $http.get("rest/competition/" + $routeParams.competitionId)
            .success(function (date) {
                $scope.competition = date;
            })
            .error(function (err, status) {
                alert(err + ' ' + status);
            });
        
        $scope.competitionId = app.getRandomUuid();
    });

app.controller('CompetitionListController', function ($scope, $http) {

    $http.get("rest/competition").success(function (data) {
        for (i = 0; i < data.length; ++i)
            data[i].timeObject = new Date(data[i].time);

        $scope.competitions = data;
    }).error(function (err, status) {
        alert(status);
    });

    $scope.selectedItemId= null;

    $scope.onTimeChange = function(c) {
        c.time = c.timeObject.getTime();
        alert('Time changed. New time: ' + unixTimeToString(c.time));
    }

    $scope.unixTimeToString = function(unixTime) {
        var date = new Date(unixTime);
        return date.toLocaleString();
    }

    $scope.onSelectionRequest = function(c, i, e) {
        console.log("SELECT: Competition id:" + c.id + ". Index: " + i);
    }

    $scope.onDeleteRequest = function(c, i, e) {
        console.log("DELETE: Competition id: " + c.id + ". Index: " + i);
        console.log("    i=" + i);

        $http.delete("rest/competition/" + c.id).success(function () {
            $scope.competitions.splice(i, 1);
        }).error(function (err) {
            alert("Deleting competition failed: " + err.statusText);
        });
    }

    $scope.sortCriteria = 'time';
});