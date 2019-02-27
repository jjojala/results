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

def test_remove_communities(client):

    events = [
        { 'id':'e-1', 'date':'2019-02-27T23:17:00.000+03:00', 'name':'POM' }
    ]

    for e in events:
        assert 201 == client.post('/api/event/' + e['id'], json=e).status_code

    communities = [
        { 'id':'pirhi', 'name':'Pirkkalan Hiihtäjät', 'abbr':'PirHi' },
        { 'id':'koovee', 'name':'Koovee', 'abbr':'Koovee' }
    ]

    for c in communities:
        assert 201 == client.post('/api/community/' + c['id'], json=c).status_code

    names = [
        { 'id':'nm-1', 'gn':'Jari', 'fn':'Ojala', 'rc':'koovee' }
    ]

    for n in names:
        assert 201 == client.post('/api/name/' + n['id'], json=n).status_code

    competitors = [
        { 'id':'cm-1', 'eid':'e-1', 'nid':'nm-1', 'cid':'pirhi' }
    ]

    for c in competitors:
        assert 201 == client.post('/api/competitor/' + c['id'], json=c).status_code

    # rc (recent community) just cleared from the names referring to this one
    assert 200 == client.delete('/api/community/koovee').status_code
    assert [] == client.get('/api/name/?rc=koovee').get_json()

    # prohibited, as competitor refers to this community
    assert 409 == client.delete('/api/community/pirhi').status_code
    
    
def test_get_communities_400(client):
    r = client.get('/api/community/?foo=bar')
    assert 400 == r.status_code

def test_get_communities(client):
    """Get all Communities."""

    result = client.get("/api/community/")
    assert 200 == result.status_code
    assert [] == result.get_json()

    result = client.post("/api/community/1", json={
        'id':'1',
        'name':'Pirkkalan Hiihtäjät',
        'abbr':'PirHi'
        })
    assert 201 == result.status_code

    result = client.get("/api/community/")
    assert 200 == result.status_code
    data = result.get_json()
    assert 1 == len(data)
    assert '1' == data[0]['id']
    assert 'Pirkkalan Hiihtäjät' == data[0]['name']
    assert 'PirHi' == data[0]['abbr']

    result = client.get("/api/community/1")
    assert 200 == result.status_code
    data = result.get_json()
    assert '1' == data['id']
    assert 'Pirkkalan Hiihtäjät' == data['name']
    assert 'PirHi' == data['abbr']

    result = client.delete("/api/community/1")
    assert 200 == result.status_code

    result = client.get("/api/community/")
    assert 200 == result.status_code
    assert [] == result.get_json()
    
