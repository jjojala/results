/* 
 * Copyright (C) 2016 Jari Ojala (jari.ojala@iki.fi)
 */

(function() {
    var isTimeArrayValid = function(a) {
        // minutes < 60 && seconds < 60 (no other checks needed)
        return (a[2] < 6 && a[4] < 6);
        /*
        return (a[2] === '_' || a[2] < 6)
            && (a[4] === '_' || a[4] < 6)
            && (isDigit(a[6]));
        */
    }

    var timeArrayToMSecs = function(a) {
        return  (a[0] * 10 + a[1]) * 60 * 60 * 1000 // hours    --> msecs
            +   (a[2] * 10 + a[3]) * 60 * 1000      // minutes  --> msecs
            +   (a[4] * 10 + a[5]) * 1000           // seconds  --> msecs
            +   (a[6]) * 100;                       // tenths   --> msecs
    };

    var timeArrayToString = function(a) {
        return [
                    a[0], a[1], // hours
            ':',    a[2], a[3], // minutes
            ':',    a[4], a[5], // seconds
            '.',    a[6]        // tenths
        ].join('');
    };

    var stringToTimeArray = function(s) {
        var a = s.split('');
        return [a[0], a[1], a[3], a[4], a[6], a[7], a[9]];
    };

    var isDigit = function(ch) {
        return !(ch < '0'.charCodeAt(0) || ch > '9'.charCodeAt(0));
    };
    
    var timeEditor = function() {
        return {
            scope: {
                basetime: '@',
                ngModel: '='
            },
            template: '<input type="text"></input>',
            link: function(scope, element, attrs) {
                var input = element.children();
                var initialModel = '__:__:__._';

    /*            scope.$watch('ngModel', function(val) {
                    console.log('ngModel: ' + val);
                    initialModel = val ? val : '__:__:__._';
                    input.val(initialModel);
                });
    */
                var value = stringToTimeArray(initialModel);
                var backlog = [];
                var position = value.length - 1;

                input.val(initialModel);
                input.css('background',
                    (isTimeArrayValid(value) ? 'none' : 'red'));

                input.bind('keypress keydown', function(event) {

                    console.log('before key: ' + event.which
                        + '\t\tvalue: ' + value
                        + '\tbacklog: ' + backlog
                        + '\t\tposition: ' + position);

                    if (backlog.length > 0 && event.which === 8 /* backspace */) {
                        position = position + 1;
                        value.splice(6, 1);
                        value.splice(position, 0, backlog.pop());
                        input.val(timeArrayToString(value));
                    }

                    else if (position >= 0 && isDigit(event.which)) {
                        backlog.push(value[position]);
                        value.splice(position, 1);
                        value.push(String.fromCharCode(event.which));
                        input.val(timeArrayToString(value));
                        position = position - 1;
                    }
/*                    
                    else if (position < 0 && isDigit(event.which)) {
                        position = 6;
                        backlog = [ value[position] ];
                        value.splice(position, 1);
                        value.push(String.fromCharCode(event.which));
                        input.val(timeArrayToString(value));
                        position = position - 1;
                    }
*/
                    if (isTimeArrayValid(value)) {
                        input.css('background', 'none');
                        scope.$apply(function() {
                            scope.ngModel = ((scope.basetime ? scope.basetime : 0)
                                    + timeArrayToMSecs(value)); 
                            });
                    } else {
                        console.log('not valid!');
                        input.css('background', 'red');
                    }

                    event.preventDefault();

                    console.log('after\t\t'
                        + '\t\tvalue: ' + value
                        + '\tbacklog: ' + backlog
                        + '\t\tposition: ' + position);
                });
            }
        };
    };
    
    angular.module('utils').directive('timeEditor', timeEditor);
})();

