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
    r = client.get('/api/tag/?foo=bar')
    assert 400 == r.status_code

def test_tags_with_root_id(client):
    scope_1 = [
        {
            'id':'scope-1',
            'tag':'tag-scope-1',
            'desc':'desc-tag-1',
            'grp':True
        },
        {
            'id':'tag-1-1',
            'pid':'scope-1',
            'tag':'tag-tag-1-1',
            'desc':'desc-tag-1-1',
            'grp':True
        },
        {
            'id':'tag-1-1-1',
            'pid':'tag-1-1',
            'tag':'tag-tag-1-1-1',
            'desc':'desc-tag-1-1-1'
        },
        {
            'id':'tag-1-2',
            'pid':'scope-1',
            'tag':'tag-tag-1-2',
            'desc':'desc-tag-1-2'
        }
    ]

    for tag in scope_1:
        r = client.post('/api/tag/' + tag['id'], json=tag)
        assert 201 == r.status_code

    client.post('/api/tag/scope-2', json={
        'id':'scope-2',
        'tag':'tag-scope-2',
        'desc':'desc-scope-2',
        'grp':True
        })

    r = client.get('/api/tag/?ts_id=scope-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert 4 == len(d)
    for tag in d:
        assert tag['id'] in (t['id'] for t in scope_1)

    r = client.get('/api/tag/?ts_id=scope-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)

    r = client.get('/api/tag/?pid=tag-1-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)
    assert 'tag-1-1-1' == d[0]['id']

    r = client.get('/api/tag/?tag=-1-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)
    assert 'tag-tag-1-2' == d[0]['tag']

    r = client.get('/api/tag/?grp=true')
    d = r.get_json()
    assert 200 == r.status_code
    assert 3 == len(d)

    r = client.get('/api/tag/?grp=true&tag=scope-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)
    assert 'tag-scope-2' == d[0]['tag']

def test_simple_tag(client):
    """Get all Tags, while there're none yet."""
    
    result = client.get("/api/tag/")
    assert [] == result.get_json() 
    assert 200 == result.status_code

    result = client.post("/api/tag/1", json={
        'id': '1', 'tag':'#1', 'desc':'desc #1'})
    assert 201 == result.status_code

    result = client.delete("/api/tag/1")
    assert 200 == result.status_code

    result = client.get("/api/tag/")
    assert [] == result.get_json() 
    assert 200 == result.status_code

def test_tag_self_reference_prevention(client):
    result = client.post("/api/tag/1", json={
        'id':'1', 'tag':'tag-1', 'desc':'desc 1',
        'refs': [ '1' ]})
    assert 422 == result.status_code

    result = client.get("/api/tag/1")
    assert 404 == result.status_code

def test_tag_update_refs(client):
    tags = [
        { 'id':'scope-1', 'tag':'scope-1', 'desc':'desc-scope-1', 'grp':True },
        { 'id':'grp-1', 'pid':'scope-1', 'tag':'tag-grp-1', 'desc':'', 'grp':True },
        { 'id':'ref-1', 'pid':'grp-1', 'tag':'', 'desc':'' },
        { 'id':'tag-1', 'pid':'scope-1', 'tag':'', 'desc':'', 'refs': [ 'ref-1' ] }
    ]
    for t in tags:
        assert 201 == client.post('/api/tag/' + t['id'], json=t).status_code

    events = [
        { 'id':'e-1', 'date':'2019-02-28T21:22:00.000+03:00', 'name':'name-e-1', 'ts_id':'scope-1' }
    ]
    for e in events:
        assert 201 == client.post('/api/event/' + e['id'], json=e).status_code

    names = [
        { 'id':'n-1', 'gn':'gn-n-1', 'fn':'fn-n-1' },
        { 'id':'n-2', 'gn':'gn-n-2', 'fn':'fn-n-2' }
    ]
    for n in names:
        assert 201 == client.post('/api/name/' + n['id'], json=n).status_code

    competitors = [
        { 'id':'c-1', 'eid':'e-1', 'nid':'n-1', 'tags': [ 'ref-1', 'tag-1' ] },
        { 'id':'c-2', 'eid':'e-1', 'nid':'n-2', 'tags': [ 'ref-1' ] }
    ]
    for c in competitors:
        assert 201 == client.post('/api/competitor/' + c['id'], json=c).status_code

    assert 200 == client.delete("/api/tag/grp-1").status_code
    assert 404 == client.get("/api/tag/grp-1").status_code
    assert 404 == client.get("/api/tag/ref-1").status_code
    assert 200 == client.get("/api/tag/scope-1").status_code
    r = client.get('/api/competitor/c-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert [ 'tag-1' ] == d['tags']
    r = client.get('/api/competitor/c-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert [] == d['tags']

    r = client.get("/api/tag/tag-1")
    assert 200 == r.status_code
    assert [] == r.get_json()["refs"]

    assert 200 == client.delete("/api/tag/scope-1").status_code
    assert 404 == client.get("/api/tag/tag-1").status_code
    r = client.get('/api/event/e-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert d['ts_id'] == None
    r = client.get('/api/competitor/c-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert [] == d['tags']

    r = client.get("/api/tag/")
    assert 200 == r.status_code
    assert [] == r.get_json()
