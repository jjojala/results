/*
 * Copyright (C) 2015-2016 Jari Ojala (jari.ojala@iki.fi).
 */

define(function() {
    'use strict';

    angular.module('app.competition').controller('CompetitionMainController',
        [ '$scope', '$http', '$routeParams', 'Uuid', 'Rcnp',
            CompetitionMainController ]);
        
    function CompetitionMainController($scope, $http, $routeParams, Uuid, Rcnp) {
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
                            console.log('TODO: recalulcate results on event time change.');
                        }
                        $scope.competition = c;
                    }
                });
            },
            'UPDATED', 'org.gemini.results.model.Event');

        Rcnp.register(function (c) {
                $scope.$apply(function() {
                    if (c.id === $scope.competition.id) {
                        alert('This event is unexpectedly removed! ' +
                            'Please return back to event list.');
                        $scope.current = { clazz: null, group: null, competitor: null };
                        $scope.competition = null;
                        $scope.groups = [];
                        $scope.clazzes = [];
                        $scope.competitors = [];
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Event');
            
        Rcnp.register(function (g) {
                $scope.$apply(function() {
                    if (g.eventId === $scope.competition.id) {
                        $scope.groups.push(g);
                    }
                });
            },
            'CREATED', 'org.gemini.results.model.Group');

        Rcnp.register(function (g) {
                $scope.$apply(function() {
                    if (g.eventId === $scope.competition.id) {
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
                    if (g.eventId === $scope.competition.id) {
                        for (var i = 0; i < $scope.groups.length; i++) {
                            if (g.id === $scope.groups[i].id) {
                                $scope.groups.splice(i, 1);
                                break;
                            }
                        }

                        for (i = 0; i < $scope.clazzes.length; i++) {
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
                    if (cl.eventId === $scope.competition.id) {
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
                    if (cl.eventId === $scope.competition.id) {
                        for (var i = 0; i < $scope.clazzes.length; i++) {
                            if (cl.id === $scope.clazzes[i]._ref.id) {
                                $scope.clazzes[i] = {
                                    _group: getGroupById(cl.groupId),
                                    _ref: cl
                                };

                                if (cl.offset !== $scope.clazzes[i]._ref.offset) {
                                    console.log('TODO: Recalculate results on Clazz change');
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
                    if (cl.eventId === $scope.competition.id) {
                        for (var i = 0; i < $scope.clazzes.length; i++) {
                            if (cl.id === $scope.clazzes[i]._ref.id) {
                                $scope.clazzes.splice(i, 1);
                                break;
                            }
                        }

                        for (i = 0; i < $scope.competitors.length; i++) {
                            if ($scope.competitors[i]._clazz &&
                                    cl.id === $scope.competitors[i]._clazz.id) {
                                $scope.competitors[i]._clazz = null;

                                console.log('TODO: Remove results on Clazz remove');
                            }
                        }
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Clazz');

        Rcnp.register(function (co) {
                $scope.$apply(function() {
                    if (co.eventId === $scope.competition.id) {
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
                    if (co.eventId === $scope.competition.id) {
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
                    if (co.eventId === $scope.competition.id) {
                        for (var i = 0; i < $scope.competitors.length; i++) {
                            if (co.id === $scope.competitors[i]._ref.id) {
                                $scope.competitors.splice(i, 1);
                                break;
                            }
                        }
                    }
                });
            },
            'REMOVED', 'org.gemini.results.model.Competitor');

        var baseUrl = 'rest/events/' + $routeParams.competitionId;
        
        $http.get(baseUrl)
            .success(function (data) {
                $scope.competition = data;
        
                $http.get(baseUrl + "/groups/")
                    .success(function (data) {
                        $scope.groups = data;
                
                        $http.get(baseUrl + "/classes/")
                            .success(function (data) {
                                var _clazzes = [];
                                for (var i = 0; i < data.length; i++)
                                    _clazzes.push({
                                        _group: getGroupById(data[i].groupId),
                                        _ref: data[i]
                                    });
                                $scope.clazzes = _clazzes;

                                $http.get(baseUrl + "/competitors/")
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
            $http.post(baseUrl + "/groups/" + g.id, g)
                .success(function() { $scope.current.group = null; })
                .error(function(err, status) {
                    alert("Adding group failed: \nerr: " + err + "\nstatus: " +
                        status + "\nGroup: " + angular.toJson(g, true));
                });
        };        
        
        $scope.onGroupDestroy = function(g, i) {
            $http.delete(baseUrl +  "/groups/" + g.id)
                .success(function () {
                    $scope.current.group = null;
                }).error(function (err) {
                    alert("Deleting group failed: " + err.statusText);
                });
        };

        $scope.onGroupUpdate = function(g) {
            $http.put(baseUrl + "/groups/" + g.id, g)
                .success(function() { $scope.current.group = null; })
                .error(function(err, status) {
                    alert("Updating group failed: \nerr: " + err +
                        "\nstatus: " + status + "\nGroup: " +
                        angular.toJson(g, true));
                });
        };
        
        $scope.onGroupStart = function(g) {
            g.offset = Date.now() - $scope.competition.time;
        };

        $scope.onClazzCreate = function(c) {
            c._ref.id = Uuid.randomUUID();
            c._ref.groupId = c._group.id;

            $http.post(baseUrl + "/classes/" + c._ref.id, c._ref)
                .success(function() { $scope.current.clazz = null; })
                .error(function(err, status) {
                    alert("Adding class failed: \nerr: " + err + "\nstatus: " +
                        status + "\nClass: " + angular.toJson(c, true));
                });
        };

        $scope.onClazzDestroy = function(c) {
            $http.delete(baseUrl + "/classes/" + c._ref.id)
                .error(function (err) {
                    alert("Deleting class failed: " + err.statusText);
                });
        };

        $scope.onClazzUpdate = function(c) {
            c._ref.groupId = c._group.id;
            $http.put(baseUrl + "/classes/" + c._ref.id, c._ref)
                .success(function() { $scope.current.clazz = null; })
                .error(function(err, status) {
                    alert("Updating class failed: \nerr: " + err +
                        "\nstatus: " + status + "\nClass: " +
                        angular.toJson(c, true));
                });
        };

        $scope.onClazzStart = function(c) {
            c._ref.offset = Date.now() - getGroupStart(c._group);
        };

        $scope.onCompetitorCreate = function(c) {
            c._ref.id = Uuid.randomUUID();
            c._ref.clazzId = c._clazz._ref.id;

            $http.post(baseUrl + "/competitors/" + c._ref.id, c._ref)
                .success(function() { $scope.current.competitor = null; })
                .error(function (err, status) {
                    alert("Adding competitor failed: \nerr: " + err +
                        "\nstatus: " + status + "\nCompetitor:" +
                        angular.toJson(c, true));
                });
        };

        $scope.onCompetitorDestroy = function(c) {
            $http.delete(baseUrl + "/competitors/" + c._ref.id)
                .error(function (err) {
                    alert("Deleting competitor failed: " + err.statusText);
                });
        };

        $scope.onCompetitorUpdate = function(c) {
            c._ref.clazzId = c._clazz._ref.id;

            $http.put(baseUrl + "/competitors/" + c._ref.id, c._ref)
                .success(function() { $scope.current.competitor = null; })
                .error(function(err, status) {
                    alert("Updating competitor failed: \nerr: " + err +
                        "\nstatus: " + status + "\nCompetitor: " +
                        angular.toJson(c._ref, true));
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

});