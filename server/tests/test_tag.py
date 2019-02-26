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

def test_tags_with_root_id(client):
    client.post('/api/tag/scope-1', json={
        'id':'scope-1',
        'tag':'',
        'desc':'',
        'grp':True
        })
    client.post('/api/tag/tag-1-1', json={
        'id':'tag-1-1',
        'pid':'scope-1',
        'tag':'',
        'desc':'',
        'grp':True
        })
    client.post('/api/tag/tag-1-1-1', json={
        'id':'tag-1-1-1',
        'pid':'tag-1-1',
        'tag':'',
        'desc':''
        })
    client.post('/api/tag/tag-1-2', json={
        'id':'tag-1-2',
        'pid':'scope-1',
        'tag':'',
        'desc':''
        })
    client.post('/api/tag/scope-2', json={
        'id':'scope-2',
        'tag':'',
        'desc':'',
        'grp':True
        })

    r = client.get('/api/tag/?root_id=scope-1')
    d = r.get_json()
    assert 200 == r.status_code
    assert 4 == len(d)

    r = client.get('/api/tag/?root_id=scope-2')
    d = r.get_json()
    assert 200 == r.status_code
    assert 1 == len(d)

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
    result = client.post("/api/tag/scope-1", json={
        'id':'scope-1',
        'tag':'scope-1',
        'desc':'desc-scope-1'})
    assert 201 == result.status_code

    result = client.post("/api/tag/grp-1", json={
        'id':'grp-1',
        'pid':'scope-1',
        'tag':'',
        'desc':'',
        'grp':True})
    assert 201 == result.status_code

    result = client.post("/api/tag/ref-1", json={
        'id':'ref-1',
        'pid':'grp-1',
        'tag':'',
        'desc':''})
    assert 201 == result.status_code

    result = client.post("/api/tag/tag-1", json={
        'id':'tag-1',
        'pid':'scope-1',
        'tag':'',
        'desc':'',
        'refs': [ 'ref-1' ]})
    assert 201 == result.status_code

    result = client.delete("/api/tag/grp-1")
    assert 200 == result.status_code
    result = client.get("/api/tag/grp-1")
    assert 404 == result.status_code
    result = client.get("/api/tag/ref-1")
    assert 404 == result.status_code
    result = client.get("/api/tag/scope-1")
    assert 200 == result.status_code

    result = client.get("/api/tag/tag-1")
    assert 200 == result.status_code
    assert [] == result.get_json()["refs"]

    result = client.delete("/api/tag/scope-1")
    assert 200 == result.status_code
    result = client.get("/api/tag/tag-1")
    assert 404 == result.status_code

    result = client.get("/api/tag/")
    assert 200 == result.status_code
    assert [] == result.get_json()
