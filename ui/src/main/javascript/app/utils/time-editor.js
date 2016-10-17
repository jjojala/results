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
    };
    
    var msecsToTimeArray = function(t) {
        if (!t)
            return null;
        
        var tenths = Math.round(t / 100) % 10;
        var seconds = Math.floor(t / 1000) % 60;
        var minutes = Math.floor(t / 60000) % 60;
        var hours = Math.floor(t / (3600000));
        
        return [
            Math.floor(hours / 10).toString(), (hours % 10).toString(),
            Math.floor(minutes / 10).toString(), (minutes % 10).toString(),
            Math.floor(seconds / 10).toString(), (seconds % 10).toString(),
            tenths.toString()
        ];
    };

    var timeArrayToMsecs = function(a) {
        return ((a[0] * 10 + a[1]) * 60 * 60 * 1000) + // hours    --> msecs
            ((a[2] * 10 + a[3]) * 60 * 1000) +         // minutes  --> msecs
            ((a[4] * 10 + a[5]) * 1000) +              // seconds  --> msecs
            ((a[6]) * 100);                            // tenths   --> msecs
    };

    var timeArrayToString = function(a) {
        return [
                    a[0], a[1], // hours
            ':',    a[2], a[3], // minutes
            ':',    a[4], a[5], // seconds
            '.',    a[6]        // tenths
        ].join('');
    };

    var isDigit = function(ch) {
        return !(ch < '0'.charCodeAt(0) || ch > '9'.charCodeAt(0));
    };

    var timeEditor = function() {
        
        return {
            scope: {
                time: '='
            },
            template: '<input type="text"></input>',
            link: function(scope, element) {
                var input = element.children();
                var values = [];
                var backlog = [];
                var position = 6;
                var basetime = null;
                
                scope.$watch('time', function(time) {

                    basetime = scope.time ?
                        new Date(scope.time).setHours(0, 0, 0, 0) : new Date(0);

                    console.log('scope.time    : ' + 
                                (scope.time ? new Date(scope.time) : basetime) +
                                '\nbasetime: ' + new Date(basetime));

                    values = msecsToTimeArray(scope.time - basetime);
                    input.val(values ? timeArrayToString(values) : null);
                    backlog = [];
                    position = 6;
                });
                
                input.bind('keypress keydown', function(event) {
                    if (backlog.length > 0 && event.which === 8) { // backspace
                        position = position + 1;
                        values.splice(6, 1);
                        values.splice(position, 0, backlog.pop());
                        input.val(timeArrayToString(values));
                    }

                    else if (position >= 0 && isDigit(event.which)) {
                        backlog.push(values[position]);
                        values.splice(position, 1);
                        values.push(String.fromCharCode(event.which));
                        input.val(timeArrayToString(values));
                        position = position - 1;
                    }

                    if (!(event.which === 9 || event.which === 13))
                        event.preventDefault();
                });

                input.bind('blur', function() {
                    if (isTimeArrayValid(values)) {
                        scope.$apply(function() {
                            scope.time =
                                    basetime + timeArrayToMsecs(values);
                            console.log('time:  ' + new Date(timeArrayToMsecs(values)));
                            console.log('basetime: ' + basetime);
                        });
                    } else {
                    }
                });
            }
        };
    };
    
    angular.module('utils').directive('timeEditor', timeEditor);
})();

