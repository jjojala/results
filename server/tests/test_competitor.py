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
    r = client.get('/api/competitor/?foo=bar')
    assert 400 == r.status_code

def test_get_competitions_by_multipe_tags(client):
    tags = [
        { 'id':'scope-1', 'tag':'tag-scope-1', 'desc':'desc-scope-1', 'grp':True },
        { 'id':'t-1', 'pid':'scope-1', 'tag':'tag-t-1', 'desc':'desc-t-1' },
        { 'id':'t-2', 'pid':'scope-1', 'tag':'tag-t-2', 'desc':'desc-t-2' },
        { 'id':'t-3', 'pid':'scope-1', 'tag':'tag-t-3', 'desc':'desc-t-3' }
    ]

    for t in tags:
        assert 201 == client.post('/api/tag/' + t['id'], json=t).status_code
    
    events = [
        { 'id':'e-1', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'name-e-1', 'ts_id':'scope-1'},
        { 'id':'e-2', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'name-e-2', 'ts_id':'scope-1'}
    ]

    for e in events:
        assert 201 == client.post('/api/event/' + e['id'], json=e).status_code

    names = [
        { 'id':'n-1', 'gn':'Jari', 'fn':'Ojala' },
        { 'id':'n-2', 'gn':'Jenny', 'fn':'Ojala' },
        { 'id':'n-3', 'gn':'Aleksi', 'fn':'Ojala' }
    ]

    for n in names:
        assert 201 == client.post('/api/name/' + n['id'], json=n).status_code

    competitors = [
        { 'id':'c-1', 'eid':'e-1', 'nid':'n-1', 'tags': [ 't-1', 't-2', 't-3' ] },
        { 'id':'c-2', 'eid':'e-1', 'nid':'n-2', 'tags': [ 't-1', 't-2' ] },
        { 'id':'c-3', 'eid':'e-2', 'nid':'n-1', 'tags': [ 't-1' ] },
        { 'id':'c-4', 'eid':'e-2', 'nid':'n-2', 'tags': [ 't-2', 't-3' ] },
        { 'id':'c-5', 'eid':'e-1', 'nid':'n-3', 'tags': [ 't-3' ] }
        ]

    for c in competitors:
        r = client.post('/api/competitor/' + c['id'], json=c)
        assert 201 == r.status_code

    r = client.get('/api/competitor/?tags=t-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert 3 == len(d)

    r = client.get('/api/competitor/?tags=t-1,t-2')
    d = r.get_json()
    t_lists = [ c['tags'] for c in d ]
    assert 200 == r.status_code
    assert 2 == len(t_lists)
 
def test_get_competitors(client):
    """Get all competitors."""

    result = client.get("/api/competitor/")
    assert 200 == result.status_code
    assert [] == result.get_json()

    events = [
        { 'id':'2', 'date':'2019-02-26T23:21:00.000+03:00', 'name':'name-e-2'}
    ]
    for e in events:
        assert 201 == client.post('/api/event/' + e['id'], json=e).status_code

    names = [
        { 'id':'3', 'gn':'Donald', 'fn':'Duck' }
    ]

    for n in names:
        assert 201 == client.post('/api/name/' + n['id'], json=n).status_code

    result = client.post("/api/competitor/1", json={
        'id':'1',
        'eid':'2',
        'nid':'3'
        })
    assert 201 == result.status_code

    result = client.get("/api/competitor/")
    assert 200 == result.status_code
    data = result.get_json()
    assert 1 == len(data)
    assert '1' == data[0]['id']
    assert '2' == data[0]['eid']
    assert '3' == data[0]['nid']

    result = client.get("/api/competitor/1")
    assert 200 == result.status_code
    data = result.get_json()
    assert '1' == data['id']
    assert '2' == data['eid']
    assert '3' == data['nid']

    result = client.delete("/api/competitor/1")
    assert 200 == result.status_code

    result  = client.get("/api/competitor/")
    assert 200 == result.status_code
    assert [] == result.get_json()
