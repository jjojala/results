/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', [ 'datePicker', 'ui.bootstrap' ]);

app.controller('CompetitionSelectionController', function ($scope, $http) {

    $http.get("rest/competition").success(function (data) {
        for (i = 0; i < data.length; ++i)
            data[i].timeObject = new Date(data[i].time);

        $scope.competitions = data;
    }).error(function (err, status) {
        alert(status);
    });

    var selectedCompetitionId = null;

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

    $scope.orderProp = 'time';
});