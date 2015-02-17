/* 
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */

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