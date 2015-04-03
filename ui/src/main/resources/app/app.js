/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', []);

app.controller('CompetitionSelectionController', function ($scope, $http) {

    $http.get("rest/competition").success(function (data) {
        $scope.competitions = data;
    }).error(function (err, status) {
        alert(status);
    });

    $scope.onDeleteRequest = function(c, i, e) {
        console.log("Delete requested for competition #" + c.id);
        console.log("    i=" + i);
    }

    $scope.orderProp = 'time';
});

function timeToString(time) {
    var date = new Date(time);
    return date.toLocaleDateString() + ' '
            + date.toLocaleTimeString();
};

function Rest(request) {

    this.create = function(url, data) {
        return request.post(url, {
            data : data,
            handleAs : 'json',
            headers : {
                'Content-Type' : 'application/json',
                'Accept' : 'application/json'
            }
        });
    }

    this.update = function(url, data) {
        return request.put(url, {
            data : data,
            handleAs : 'json',
            headers : {
                'Content-Type' : 'application/json',
                'Accept' : 'application/json'
            }
        });
    }

    this.destroy = function(url) {
        return request.del(url, {
            headers : {
                'Content-Type' : 'application/json',
                'Accept' : 'application/json'
            }
        });
    }

    this.get = function(url) {
        return request(url, {
            handleAs : 'json',
            headers : {
                'Content-Type' : 'application/json',
                'Accept' : 'application/json'
            }
        });
    }
}

function Competition(request, url, ctionId) {

    this.get = function(onSuccess, onFailuere) {
        request(url + '/competition/' + ctionId, {
            'handleAs' : 'json',
            'headers' : {
                    'Content-Type' : 'application/json',
                    'Accept' : 'application/json' 
                }
            }).then(
                function(data) {
                    onSuccess(data);
                },
                function(err) {
                    onFailure(err);
                }
            );
    }

    this.create = function(ction, onSuccess, onFailure) {
        Rest(request).create(url + '/competition/' + ctionId, c).then(
                function(data) {
                    alert('Created: ' + ction);
                    onSuccess(ction);
                },
                function(err) {
                    alert('Creation failed: ' + err);
                    onFailure(err);
                }
            );
    }

    this.destroy = function(onSuccess, onFailure) {
        Rest(request).destroy(url + '/competition/' + ctionId).then(
                function(ok) {
                    alert('Competition destroyed: ' + ctionId);
                    onSuccess(ok);
                },
                function(err) {
                    alert('Destroying competition failed: ' + err);
                    onFailure(err);
                }
            );
    }
}