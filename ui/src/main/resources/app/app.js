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
                        (!handlers[i].entityClass || handlers[i].entityClass === parts[1]))
                    handlers[i].handler(msg.data, parts[2], parts[0], parts[1]);
            }
        }
    });

    ws.$on('$error', function(err) {
        console.log('onError: ' + err);
    })

    return {
        register: function(f, e, c) {
            handlers.push({ handler: f, event: e, entityClass: c });
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

// timestamp:duration_in_msecs:format
app.filter('timestamp', function() {
    return function(duration, format) {
        if (duration) {
            // TODO: support for format
            var fractions = (duration % 1000 / 1000).toFixed(0);
            var seconds = Math.floor(duration / 1000) % 60;
            var minutes = Math.floor(duration / 60000) % 60;
            var hours = Math.floor(duration / 3600000);

            seconds = seconds < 10 ? '0' + seconds : String(seconds);
            minutes = minutes < 10 ? '0' + minutes : String(minutes);
            
            return hours + ':' + minutes + ':' + seconds + '.' + fractions;
        }
        return null;
    };
});

app.controller('CompetitionMainController',
    function($scope, $http, $routeParams, Uuid, Rcnp) {

        $scope.current = { clazz: null, group: null, competitor: null };
        $scope.competition = null;
        $scope.groups = [];
        $scope.clazzes = [];
        $scope.competitors = [];

        var getGroupById = function(groupId) {
            for (var i = 0; i < $scope.groups.length; i++) {
                if ($scope.groups[i].id === groupId)
                    return $scope.groups[i];
            }

            return null;
        };

        var getClazzById = function(clazzId) {
            for (var i = 0; i < $scope.clazzes.length; i++) {
                    if ($scope.clazzes[i]._ref.id === clazzId)
                        return $scope.clazzes[i];
                }

            return null;
        };

        // Return group's start time as milliseconds since EPOC
        // getGroupStart(group): Number
        var getGroupStart = function(g) {
            return $scope.competition.time + g.offset;
        };

        // Return clazz's start time as msecs since EPOC
        // getClazzStart(clazz): Number
        var getClazzStart = function(c) {
            return getGroupStart(c._group) + c._ref.offset;
        };
        
        // Return competitor's start time as msecs since EPOC
        // getCompetitorStart(competitor): Number
        var getCompetitorStart = function(c) {
            return getClazzStart(c._clazz) + c._ref.offset;
        };

        // getResult(competitor): Number 
        var getResult = function(c) {
            if (c._ref.finish) {
                return c._ref.finish - getCompetitorStart(c);
            } else {
                console.log('Finishtime not set!');
                return null;
            }
        };

        Rcnp.register(function(c) {
                $scope.$apply(function() {
                    if (c.id === $scope.competition.id) {
                        if (c.time !== $scope.competition.time) {
                            console.log('TODO: recalulcate results on competion time change.');
                        }
                        $scope.competition = c;
                    }
                });
            },
            'UPDATED', 'org.gemini.results.model.Competition');

        Rcnp.register(function (c) {
                $scope.$apply(function() {
                    if (c.id === $scope.competition.id) {
                        alert('This competition is unexpectedly removed! '
                            + 'Please return back to competition list.');
                        $scope.current = { clazz: null, group: null, competitor: null };
                        $scope.competition = null;
                        $scope.groups = [];
                        $scope.clazzes = [];
                        $scope.competitors = [];
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
                                if (g.offset !== $scope.groups[i].offset) {
                                    console.log('TODO: recalulate results on Group offset change!');
                                }

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

                        for (var i = 0; i < $scope.clazzes.length; i++) {
                            if (g.id === $scope.clazzes[i]._group.id) {
                                $scope.clazzes[i]._group = null;
                            
                                console.log('TODO: remove results on Group removal');
                            }
                        }
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Group');

        Rcnp.register(function (cl) {
                $scope.$apply(function() {
                    if (cl.competitionId === $scope.competition.id) {
                        $scope.clazzes.push({
                            _group: getGroupById(cl.groupId),
                            _ref: cl
                        });
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (cl) {
                $scope.$apply(function() {
                    if (cl.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.clazzes.length; i++) {
                            if (cl.id === $scope.clazzes[i]._ref.id) {
                                $scope.clazzes[i] = {
                                    _group: getGroupById(cl.groupId),
                                    _ref: cl
                                };

                                if (cl.offset !== $scope.clazzes[i]._ref.offset) {
                                    console.log('TODO: Recalculate results on Clazz change')
                                }

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
                        for (var i = 0; i < $scope.clazzes.length; i++) {
                            if (cl.id === $scope.clazzes[i]._ref.id) {
                                $scope.clazzes.splice(i, 1);
                                break;
                            }
                        }

                        for (var i = 0; i < $scope.competitors.length; i++) {
                            if ($scope.competitors[i]._clazz &&
                                    cl.id === $scope.competitors[i]._clazz.id) {
                                $scope.competitors[i]._clazz = null;

                                console.log('TODO: Remove results on Clazz remove');
                            }
                        }
                    }
                })
            },
            'REMOVED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.competitionId === $scope.competition.id) {
                        var context = {
                            _clazz: getClazzById(co.clazzId),
                            _ref: co
                        };
                        context._result = getResult(context);

                        $scope.competitors.push(context);
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Competitor');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.competitionId === $scope.competition.id) {
                        for (var i = 0; i < $scope.competitors.length; i++) {
                            if (co.id === $scope.competitors[i]._ref.id) {
                                var context = {
                                    _clazz: getClazzById(co.clazzId),
                                    _ref: co
                                };
                                context._result = getResult(context);
                                $scope.competitors[i] = context;
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
                                var _clazzes = [];
                                for (var i = 0; i < data.length; i++)
                                    _clazzes.push({
                                        _group: getGroupById(data[i].groupId),
                                        _ref: data[i]
                                    });
                                $scope.clazzes = _clazzes;

                                $http.get(baseUrl + "/competitor/")
                                    .success(function (data) {
                                        var _competitors = [];
                                        for (var i = 0; i < data.length; i++) {
                                            var context = {
                                                _clazz: getClazzById(data[i].clazzId),
                                                _ref: data[i]
                                            };
                                            context._result = getResult(context);
                                            _competitors.push(context);
                                        }

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
        };

        $scope.onGroupUpdate = function(g) {
            $http.put(baseUrl + "/group/" + g.id, g)
                .success(function() { $scope.current.group = null; })
                .error(function(err, status) {
                    alert("Updating group failed: \nerr: " + err + "\nstatus: "
                            + status + "\nGroup: " + angular.toJson(g, true));
                });
        };
        
        $scope.onGroupStart = function(g) {
            g.offset = Date.now() - $scope.competition.time;
        };

        $scope.onClazzCreate = function(c) {
            c._ref.id = Uuid.randomUUID();
            c._ref.groupId = c._group.id;

            $http.post(baseUrl + "/class/" + c._ref.id, c._ref)
                .success(function() { $scope.current.clazz = null; })
                .error(function(err, status) {
                    alert("Adding class failed: \nerr: " + err + "\nstatus: "
                            + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onClazzDestroy = function(c) {
            $http.delete(baseUrl + "/class/" + c._ref.id)
                .error(function (err) {
                    alert("Deleting class failed: " + err.statusText);
                });
        };

        $scope.onClazzUpdate = function(c) {
            c._ref.groupId = c._group.id;
            $http.put(baseUrl + "/class/" + c._ref.id, c._ref)
                .success(function() { $scope.current.clazz = null; })
                .error(function(err, status) {
                    alert("Updating class failed: \nerr: " + err + "\nstatus: "
                    + status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onClazzStart = function(c) {
            c._ref.offset = Date.now() - getGroupStart(c._group);
        };

        $scope.onCompetitorCreate = function(c) {
            c._ref.id = Uuid.randomUUID();
            c._ref.clazzId = c._clazz._ref.id;

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
        };

        $scope.onCompetitorUpdate = function(c) {
            c._ref.clazzId = c._clazz._ref.id;

            $http.put(baseUrl + "/competitor/" + c._ref.id, c._ref)
                .success(function() { $scope.current.competitor = null; })
                .error(function(err, status) {
                    alert("Updating competitor failed: \nerr: " + err + "\nstatus: "
                        + status + "\nCompetitor: " + angular.toJson(c._ref, true));
                });
        };

        $scope.onCompetitorSelect = function(c) {
            $scope.current.competitor = {
                _clazz: c._clazz,
                _ref: JSON.parse(JSON.stringify(c._ref))
            };
            console.log('onCompetitorSelect: ' + angular.toJson($scope.current.competitor));
        };

        $scope.onCompetitorStart = function(c) {
            c._ref.offset = Date.now() - getClazzStart(c._clazz);
        };

        $scope.onCompetitorFinish = function(c) {
            c._ref.finish = Date.now();
            c._result = getResult(c);
        };

        $scope.onClazzSelect = function(c) {
            $scope.current.clazz = {
                _group: c._group,
                _ref: JSON.parse(JSON.stringify(c._ref))
            };
        };

        $scope.onGroupSelect = function(g) {
            $scope.current.group = JSON.parse(JSON.stringify(g));
        };

        $scope.debug = function(what) {
            console.log('DEBUG: ' + angular.toJson(what, true));
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