/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', [ 
    'ngRoute', 'ui.bootstrap', 'ngWebsocket']);

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
                });
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

app.directive('rs-typeahead', function() {
    return {
        replace: true,
        restrict: 'A',
        scope: {
            ngModel: '=',
            typehead: '=',
            placeholder: '='
        },
        template: "<input type='text' ng-model='ng-model' "
            + "typehead-min-length='0' typeahead='typehead' "
            + " placeholder='placeholder' bs-typeahead/>"
    };
});

app.controller('CompetitionMainController',
    function($scope, $http, $routeParams, $websocket, Uuid) {

        $scope.current = { class: null, group: null, competitor:null };

        $http.get("rest/competition/" + $routeParams.competitionId)
            .success(function (data) {
                $scope.competition = data;
                $http.get("rest/competition/" + $scope.competition.id
                        + "/competitor/")
                    .success(function (data) {
                        $scope.competitors = data;
                    })
                    .error(function (err, status) {
                        alert(err + ' ' + status);
                    });
                $http.get("rest/competition/"
                        + $scope.competition.id
                        + "/group/")
                    .success(function (data) {
                        $scope.groups = data;
                    })
                    .error(function (err, status) {
                        alert(err + ' ' + status);
                    });
                $http.get("rest/competition/" + $scope.competition.id
                        + "/class/")
                    .success(function (data) {
                        $scope.classes = data;
                    })
                    .error(function (err, status) {
                        alert(err + ' ' + status);
                    });
            })
            .error(function (err, status) {
                alert(err + ' ' + status);
            });

        $scope.onGroupCreate = function(g) {
            g.id = Uuid.randomUUID();
            $http.post("rest/competition/" + $scope.competition.id
                    + "/group/" + g.id, g)
                .success(function() {
                    $scope.groups.push(g);
                })
                .error(function(err, status) {
                    alert("Adding group failed: \nerr: " + err + "\nstatus: "
                            + status + "\nGroup: " + angular.toJson(g, true));
                });
        };        

        $scope.onClassCreate = function(c, g) {
            c.id = Uuid.randomUUID();
            c.groupId = g.id;

            $http.post("rest/competition/" + $scope.competition.id
                    + "/class/" + c.id, c)
                .success(function() {
                    $scope.classes.push(c);
                })
                .error(function(err, status) {
                    alert("Adding class failed: \nerr: " + err + "\nstatus: "
                            + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onCompetitorCreate = function(c) {
            c.id = Uuid.randomUUID();
            alert('TODO: Create: ' + angular.toJson(c, true));
            $http.post("rest/competition/" + $scope.competition.id
                    + "/competitor/" + c.id, c).success(function() {
                $scope.competitors.push(c);
            }).error(function (err, status) {
                alert("Adding competitor failed: \nerr: " + err + "\nstatus: "
                        + status + "\nCompetitor:"+ angular.toJson(c, true));
            });
        };

        $scope.onGroupDestroy = function(g, i) {
            $http.delete("rest/competition/" + $scope.competition.id
                    + "/group/" + g.id)
                .success(function () {
                    $scope.groups.splice(i, 1);
                }).error(function (err) {
                    alert("Deleting group failed: " + err.statusText);
                });
        }

        $scope.onClassDestroy = function(c, i) {
            $http.delete("rest/competition/" + $scope.competition.id
                    + "/class/" + c.id)
                .success(function () {
                    $scope.classes.splice(i, 1);
                }).error(function (err) {
                    alert("Deleting class failed: " + err.statusText);
                });
        }

        $scope.onCompetitorDestroy = function(c, i) {
            $http.delete("rest/competition/" + $scope.competition.id
                    + "/competitor/" + c.id)
                .success(function () {
                    $scope.competitors.splice(i, 1);
                }).error(function (err) {
                    alert("Deleting competitor failed: " + err.statusText);
                });
        }

        $scope.onCompetitorUpdate = function(c, clz) {
            c.clazzId = clz.id;
            $http.put("rest/competition/" + $scope.competition.id
                    + "/competitor/" + c.id, c)
                .error(function(err, status) {
                    alert("Updating competitor failed: \nerr: " + err + "\nstatus: "
                        + status + "\nCompetitor: " + angular.toJson(c, true));
                });
        };

        $scope.onClassUpdate = function(c, g) {
            c.groupId = g.id;
            $http.put("rest/competition/" + $scope.competition.id
                    + "/class/" + c.id, c)
                .error(function(err, status) {
                    alert("Updating class failed: \nerr: " + err + "\nstatus: "
                    + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onGroupUpdate = function(g) {
            $http.put("rest/competition/" + $scope.competition.id
                    + "/group/" + g.id, g)
                .error(function(err, status) {
                    alert("Updating group failed: \nerr: " + err + "\nstatus: "
                            + status + "\nGroup: " + angular.toJson(g, true));
                });
        };        

        $scope.onCompetitorSelect = function(c) {
            $scope.current.competitor = c;
        };

        $scope.onClassSelect = function(c) {
            $scope.current.class = c;
        };

        $scope.onGroupSelect = function(g) {
            $scope.current.group = g;
        };

        $scope.getClassName = function(classes, id) {
            if (classes) {
                for (i = 0; i < classes.length; ++i) {
                    if (classes[i].id === id)
                        return classes[i].name;
                }
            }
            return id;
        };

        $scope.getGroupName = function(groups, id) {
            if (groups) {
                for (i = 0; i < groups.length; ++i) {
                    if (groups[i].id === id)
                        return groups[i].name;
                }
            }
            return id;       
        };
    });

app.controller('CompetitionListController', function ($scope, $http, $websocket, Uuid) {

    $scope.current = {};
    $scope.competitions = [];

    var wsUrl = 
            (window.location.protocol === 'https:' ? 'wss://' : 'ws://')
            + window.location.host + '/notifications/' + Uuid.randomUUID();

    var ws = $websocket.$new(wsUrl, [ 'x-rcnp' ]);
    ws.$on('$open', function(data) {
        console.log('opened: ' + wsUrl);
    });

    ws.$on('$close', function(data) {
        console.log('onClose: ' + data);
    });

    ws.$on('$message', function(msg) {
        console.log('onMessage: ' + JSON.stringify(msg));
    });

    ws.$on('$error', function(err) {
        console.log('onError: ' + err);
    })
    

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