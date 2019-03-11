# -*- coding: utf-8 -*-
"""
   Copyright 2019 Jari ojala (jari.ojala@iki.fi)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
from .test_common import client, app
import pytest

def test_illegal_query_param(client):
    r = client.get('/api/event/?foo=bar')
    assert 400 == r.status_code

def test_get_events_by_tag_scope_id(client):
    scopes = [
        { 'id':'scope-1', 'tag':'tag-scope-1', 'desc':'desc-scope-1', 'grp':True },
        { 'id':'scope-2', 'tag':'tag-scope-2', 'desc':'desc-scope-2', 'grp':True }
    ]

    for s in scopes:
        r = client.post('/api/tag/' + s['id'], json=s)
        assert 201 == r.status_code

    events = [
        { 'id':'1', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'', 'ts_id':'scope-1'},
        { 'id':'2', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'', 'ts_id':'non-existing'},
        { 'id':'3', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'', 'ts_id':'scope-2'}
    ]

    assert 201 == client.post('/api/event/1', json=events[0]).status_code
    assert 422 == client.post('/api/event/2', json=events[1]).status_code
    assert 201 == client.post('/api/event/3', json=events[2]).status_code

    r = client.get('/api/event/?ts_id=scope-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)

    r = client.get('/api/event/?ts_id=scope-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)
    
def test_get_events(client):
    """Get all events."""

    result = client.get("/api/event/")
    assert 200 == result.status_code
    assert [] == result.get_json()

    result = client.post("/api/event/1", json={
        'id':'1',
        'date':'2019-02-25T17:00:00.000+03:00',
        'name':'Portugal O-Meeting, day 1'
        })
    assert 201 == result.status_code

    result = client.get("/api/event/")
    assert 200 == result.status_code
    data = result.get_json()
    assert 1 == len(data)
    assert '1' == data[0]['id']
    assert '2019-02-25T17:00:00.000+03:00' == data[0]['date']
    assert 'Portugal O-Meeting, day 1' == data[0]['name']

    result = client.get("/api/event/1")
    assert 200 == result.status_code
    data = result.get_json()
    assert '1' == data['id']
    assert '2019-02-25T17:00:00.000+03:00' == data['date']
    assert 'Portugal O-Meeting, day 1' == data['name']

    result = client.delete("/api/event/1")
    assert 200 == result.status_code

    result = client.get("/api/name/")
    assert 200 == result.status_code
    assert [] == result.get_json()

def test_remove_event(client):

    tags = [
        { 'id':'t-1', 'tag':'tag-t-1', 'desc':'desc-t-1', 'grp':True }
    ]
    for t in tags:
        assert 201 == client.post('/api/tag/' + t['id'], json=t).status_code

    events = [
        { 'id':'e-1', 'date':'2019-03-01T23:15:00.000+03:00', 'name':'name-e-1', 'ts_id':'t-1' }
    ]
    for e in events:
        assert 201 == client.post('/api/event/' + e['id'], json=e).status_code

    names = [
        { 'id':'n-1', 'gn':'gn-n-1', 'fn':'fn-n-1' }
    ]
    for n in names:
        assert 201 == client.post('/api/name/' + n['id'], json=n).status_code

    competitors = [
        { 'id':'c-1', 'eid':'e-1', 'nid':'n-1', 'tags': [ 't-1' ] }
    ]
    for c in competitors:
        assert 201 == client.post('/api/competitor/' + c['id'], json=c).status_code

    assert 200 == client.delete('/api/event/e-1').status_code
    assert 404 == client.get('/api/tag/t-1').status_code
    assert 404 == client.get('/api/competitor/c-1').status_code
