/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', []);

app.controller('CompetitionSelectionController', function ($scope) {
    $scope.competitions = [
        {'name': 'BB DDEE', 'id': '1', 'time': '2015-05-14T11:00:00.000+03:00'},
        {'name': 'CC EEFF', 'id': '2', 'time': '2015-04-02T00:55:40.000+03:00'},
        {'name': 'AA FFDD', 'id': '3', 'time': '1970-06-12T13:00:00.000+03:00'}
    ];

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