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

app.service('Rcnp', function(Uuid, $websocket) {
    
    var wsUrl = 
            (window.location.protocol === 'https:' ? 'wss://' : 'ws://')
            + window.location.host + '/notifications/' + Uuid.randomUUID();

    var ws = $websocket.$new(wsUrl, [ 'x-rcnp' ]);
    ws.$on('$open', function() {
        console.log('Rcnp: WebSocket opened for ' + wsUrl);
    });

    ws.$on('$close', function() {
        console.log('Rcnp: WebSocket closed.');
    });

    var handlers = [];
    
    ws.$on('$message', function(msg) {
        if (msg.event) {
            var parts = msg.event.split(/\s+/);
            
            for (var i = 0; i < handlers.length; i++) {
                if ((!handlers[i].event || handlers[i].event === parts[0]) &&
                        (!handlers[i].class || handlers[i].class === parts[1]))
                    handlers[i].handler(msg.data, parts[2], parts[0], parts[1]);
            }
        }
    });

    ws.$on('$error', function(err) {
        console.log('onError: ' + err);
    })

    return {
        register: function(f, e, c) {
            handlers.push({ handler: f, event: e, class: c });
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
    function($scope, $http, $routeParams, Uuid, Rcnp) {

        $scope.current = { class: null, group: null, competitor: null };
        $scope.competition = null;
        $scope.groups = [];
        $scope.classes = [];
        $scope.competitors = [];

        var getClassName = function(classes, id) {
            if (classes) {
                for (i = 0; i < classes.length; ++i) {
                    if (classes[i].id === id)
                        return classes[i].name;
                }
            }
            return id;
        };

        var getClassId = function(classes, name) {
            if (classes) {
                for (var i = 0; classes.length; i++) {
                    if (name.name === classes[i].name)
                        return classes[i].id;
                }
            }
            
            console.log('Cannot determine class by name: ' + name);
        }

        Rcnp.register(function(c) {
                $scope.$apply(function() {
                    if (c.id === $scope.competition.id)
                        $scope.competition = c;
                });
            },
            'UPDATED', 'org.gemini.results.model.Competition');

        Rcnp.register(function (c) {
                $scope.$apply(function() {
                    if (c.id === $scope.competition.id) {
                        alert('This competition is unexpectedly removed! '
                            + 'Please return back to competition list.');
                        $scope.current = { class: null, group: null, competitor: null };
                        $scope.competition = null;
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Competition');
            
        Rcnp.register(function (g) {
                $scope.$apply(function() {
                    if (g.competitionId === $scope.competition.id) {
                        $scope.groups.push(g);
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Group');

        Rcnp.register(function (g) {
                $scope.$apply(function() {
                    if (g.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.groups.length; i++) {
                            if (g.id === $scope.groups[i].id) {
                                $scope.groups[i] = g;
                                break;
                            }
                        }
                    }
                });
            },
            'UPDATED', 'org.gemini.results.model.Group');

        Rcnp.register(function (g) {
                $scope.$apply(function() {
                    if (g.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.groups.length; i++) {
                            if (g.id === $scope.groups[i].id) {
                                $scope.groups.splice(i, 1);
                                break;
                            }
                        }
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Group');

        Rcnp.register(function (cl) {
                $scope.$apply(function() {
                    if (cl.competitionId === $scope.competition.id) {
                        $scope.classes.push(cl);
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (cl) {
                $scope.$apply(function() {
                    if (cl.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.classes.length; i++) {
                            if (cl.id === $scope.classes[i].id) {
                                $scope.classes[i] = cl;
                                break;
                            }
                        }
                    }
                });
            },
            'UPDATED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (cl) {
                $scope.$apply(function() {
                    if (cl.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.classes.length; i++) {
                            if (cl.id === $scope.classes[i].id) {
                                $scope.classes.splice(i, 1);
                                break;
                            }
                        }
                    }
                })
            },
            'REMOVED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.competitionId === $scope.competition.id) {
                        $scope.competitors.push({
                            _clazzName: getClassName($scope.classes, co.clazzId),
                            _ref: co
                        });
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Competitor');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.competitors.length; i++) {
                            if (co.id === $scope.competitors[i]._ref.id) {
                                $scope.competitors[i] = {
                                    _clazzName: getClassName($scope.classes, co.clazzId),
                                    _ref: co
                                };
                                break;
                            }
                        }
                    }
                });
            },
            'UPDATED', 'org.gemini.results.model.Competitor');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.competitors.length; i++) {
                            if (co.id === $scope.competitors[i]._ref.id) {
                                $scope.competitors.splice(i, 1);
                                break;
                            }
                        }
                    }
                })
            },
            'REMOVED', 'org.gemini.results.model.Competitor');

        var baseUrl = 'rest/competition/' + $routeParams.competitionId;
        
        $http.get(baseUrl)
            .success(function (data) {
                $scope.competition = data;
        
                $http.get(baseUrl + "/group/")
                    .success(function (data) {
                        $scope.groups = data;
                
                        $http.get(baseUrl + "/class/")
                            .success(function (data) {
                                $scope.classes = data;

                                $http.get(baseUrl + "/competitor/")
                                    .success(function (data) {
                                        var _competitors = [];
                                        for (var i = 0; i < data.length; i++)
                                            _competitors.push({
                                                _clazzName: getClassName(
                                                        $scope.classes, data[i].clazzId),
                                                _ref: data[i]
                                            });

                                        $scope.competitors = _competitors;
                                    })
                                    .error(function (err, status) {
                                        alert(err + ' ' + status);
                                    });

                            })
                            .error(function (err, status) {
                                alert(err + ' ' + status);
                            });
                
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
            $http.post(baseUrl + "/group/" + g.id, g)
                .success(function() { $scope.current.group = null; })
                .error(function(err, status) {
                    alert("Adding group failed: \nerr: " + err + "\nstatus: "
                            + status + "\nGroup: " + angular.toJson(g, true));
                });
        };        
        
        $scope.onGroupDestroy = function(g, i) {
            $http.delete(baseUrl +  "/group/" + g.id)
                .success(function () {
                    $scope.current.group = null;
                }).error(function (err) {
                    alert("Deleting group failed: " + err.statusText);
                });
        }

        $scope.onGroupUpdate = function(g) {
            $http.put(baseUrl + "/group/" + g.id, g)
                .success(function() { $scope.current.group = null; })
                .error(function(err, status) {
                    alert("Updating group failed: \nerr: " + err + "\nstatus: "
                            + status + "\nGroup: " + angular.toJson(g, true));
                });
        };        

        $scope.onClassCreate = function(c, g) {
            c.id = Uuid.randomUUID();
            c.groupId = g.id;

            $http.post(baseUrl + "/class/" + c.id, c)
                .success(function() { $scope.current.class = null; })
                .error(function(err, status) {
                    alert("Adding class failed: \nerr: " + err + "\nstatus: "
                            + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onClassDestroy = function(c, i) {
            $http.delete(baseUrl + "/class/" + c.id)
                .error(function (err) {
                    alert("Deleting class failed: " + err.statusText);
                });
        }

        $scope.onClassUpdate = function(c, g) {
            c.groupId = g.id;
            $http.put(baseUrl + "/class/" + c.id, c)
                .success(function() { $scope.current.class = null; })
                .error(function(err, status) {
                    alert("Updating class failed: \nerr: " + err + "\nstatus: "
                    + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onCompetitorCreate = function(c) {
            c._ref.id = Uuid.randomUUID();
            c._ref.clazzId = getClassId($scope.classes, c._clazzName);

            $http.post(baseUrl + "/competitor/" + c._ref.id, c._ref)
                .success(function() { $scope.current.competitor = null; })
                .error(function (err, status) {
                    alert("Adding competitor failed: \nerr: " + err + "\nstatus: "
                        + status + "\nCompetitor:"+ angular.toJson(c, true));
                });
        };

        $scope.onCompetitorDestroy = function(c) {
            $http.delete(baseUrl + "/competitor/" + c._ref.id)
                .error(function (err) {
                    alert("Deleting competitor failed: " + err.statusText);
                });
        }

        $scope.onCompetitorUpdate = function(c) {
            console.log('c._clazzName: ' + c._clazzName);
            c._ref.clazzId = getClassId($scope.classes, c._clazzName);

            $http.put(baseUrl + "/competitor/" + c._ref.id, c._ref)
                .success(function() { $scope.current.competitor = null; })
                .error(function(err, status) {
                    alert("Updating competitor failed: \nerr: " + err + "\nstatus: "
                        + status + "\nCompetitor: " + angular.toJson(c._ref, true));
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

        $scope.getGroupName = function(groups, id) {
            if (groups) {
                for (i = 0; i < groups.length; ++i) {
                    if (groups[i].id === id)
                        return groups[i].name;
                }
            }
            return id;       
        };
    }
);

app.controller('CompetitionListController',
    function ($scope, $http, Uuid, Rcnp) {

        $scope.current = {};
        $scope.competitions = [];
        
        Rcnp.register(function(c) {
                $scope.$apply(function() {
                    $scope.competitions.push(c);
                });
            },
            'CREATED', 'org.gemini.results.model.Competition');

        Rcnp.register(function(c) {
            $scope.$apply(function() {
                for (var i = 0; i < $scope.competitions.length; i++) {
                    if ($scope.competitions[i].id == c.id) {
                        $scope.competitions[i] = c;
                        break;
                    }
                }
            });
        }, 'UPDATED', 'org.gemini.results.model.Competition');

        Rcnp.register(function(c) {
            $scope.$apply(function() {
                for (var i = 0; i < $scope.competitions.length; i++) {
                    if ($scope.competitions[i].id === c.id) {
                        $scope.competitions.splice(i, 1);
                        break;
                    }
                }
            });
        }, 'REMOVED', 'org.gemini.results.model.Competition');
        
        $http.get("rest/competition").success(function (data) {
            for (i = 0; i < data.length; ++i)
                data[i].timeObject = new Date(data[i].time);

            $scope.competitions = data;
        }).error(function (err, status) {
            alert('Retrieving competitions failed: \n'
                + 'err: ' + err + '\n'
                + 'status: ' + status);
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
            $http.post("rest/competition/" + c.id, c)
                .success(function() { $scope.current = {}; })
                .error(function(err, status) {
                    alert('Adding competition failed: \n'
                        + 'err: '  + err + '\n'
                        + 'status: ' + status + '\n'
                        + 'competition: ' + angular.toJson(c, true));
                    });
            };

        $scope.onSave = function(c) {
            delete c.timeObject;
            $http.put("rest/competition/" + c.id, c)
                .success(function() { $scope.current = {}; })
                .error(function (err, status) {
                    alert('Updating competition failed: \n'
                        + 'err: ' + err + '\n'
                        + 'status: ' + status + '\n'
                        + 'competition: ' + angular.toJson(c, true));
                    });
            };

        $scope.onDestroy = function(c, i) {
            $http.delete("rest/competition/" + c.id)
                .error(function (err, status) {
                    alert('Deleting competition failed: \n'
                        + 'err: ' + err + '\n'
                        + 'status: ' + status + '\n'
                        + 'competition: ' + angular.toJson(c, true));
                });
        };

        $scope.onDownload = function(c) {
            $http.get("rest/competition/export/" + c.id);
        }

        $scope.sortCriteria = 'time';
    }
);