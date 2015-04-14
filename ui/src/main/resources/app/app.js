/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

var app = angular.module('ResultsApplication', [ 
    'ngRoute', 'datePicker', 'ui.bootstrap' ]);

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

app.directive('editableDateField', function() {
        return {
            restrict: 'AE',
            scope: {
                required: '@',
                format: '@',
                model: '=ngModel'
            },
            template:
                  "<div>"
                + "  <div ng-hide='editMode' class='editable-date-text' "
                + "      ng-click='editMode = true'>"
                + "    {{model | date:format}}"
                + "  </div>"
                + "  <input ng-show='editMode' class='editable-date-input' "
                + "      type='datetime-local' ng-model='localModel' "
                + "      ng-required='required' ng-blur='editMode = false'/>"
                //+ "  <input type='text' date-time class='editable-date-input' "
                //+ "      format='{{format}}' ng-show='editMode' "
                //+ "      ng-model='modelDate' required='true'"
                //+ "      view='date'/>"
                + "</div>",
            link: {
                pre: function preLink(s, e, a) {
                },
                post: function postLink(s, e, a) {
                    s.editMode = false;
                    s.localModel = new Date(s.model);
                    
                    e.find('input').bind('blur', function(val) {
                        s.editMode = false;
                        s.model = s.localModel.getTime();
                        s.$digest();
                    });
                }
            }
        };
    });

app.directive('editableTextField', function() {
        return {
            restrict: 'AE',
            scope: {
                value: '=ngModel'
            },
            template:
                  "<div class='editable-text-field'>"
                + "  <div ng-show='editMode' ng-click='editMode = false'>"
                + "    {{value}}"
                + "  </div>"
                + "  <input type='text' ng-hide='editMode'"
                + "      ng-model='value' ng-blur='editMode = true'/>"
                + "</div>",
            link: function postLink(s, e, a) {
                s.editMode = true;
            }
        };
    });

app.controller('CompetitionMainController',
    function($scope, $http, $routeParams, Uuid) {
        $http.get("rest/competition/" + $routeParams.competitionId)
            .success(function (date) {
                $scope.competition = date;
            })
            .error(function (err, status) {
                alert(err + ' ' + status);
            });
        
        $scope.competitionId = Uuid.randomUUID();
    });

app.controller('CompetitionListController', function ($scope, $http) {

    $scope.newCompetition = {
        time: new Date().getTime(),
        name: "<type name here>",
        organizer: "<type organization here>"
    };

    $http.get("rest/competition").success(function (data) {
        for (i = 0; i < data.length; ++i)
            data[i].timeObject = new Date(data[i].time);

        $scope.competitions = data;
    }).error(function (err, status) {
        alert(status);
    });

    $scope.selectedItemId= null;

    $scope.onSelect = function(c) {
        alert('TODO: Selected: ' + angular.toJson(c, true));
    }

    $scope.onCreate = function(c) {
        alert('TODO: Create: ' + angular.toJson(c, true));
    }

    $scope.onDestroy = function(c, i) {
        alert('TODO: Destroy: ' + angular.toJson(c, true));
/*
        $http.delete("rest/competition/" + c.id).success(function () {
            $scope.competitions.splice(i, 1);
        }).error(function (err) {
            alert("Deleting competition failed: " + err.statusText);
        });
*/
        $scope.competitions.splice(i, 1);
    }

    $scope.sortCriteria = 'time';
});