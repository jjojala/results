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

def test_get_names_400(client):
    r = client.get('/api/name/?foo=bar')
    assert 400 == r.status_code

def test_get_names(client):
    """Get all names."""

    result = client.get("/api/name/")
    assert 200 == result.status_code
    assert [] == result.get_json()

    result = client.post("/api/name/1", json={
        'id':'1',
        'gn':'Jari',
        'fn':'Ojala'
        })
    assert 201 == result.status_code

    result = client.get("/api/name/")
    assert 200 == result.status_code
    data = result.get_json()
    assert 1 == len(data)
    assert '1' == data[0]['id']
    assert 'Jari' == data[0]['gn']
    assert 'Ojala' == data[0]['fn']

    result = client.get("/api/name/1")
    assert 200 == result.status_code
    data = result.get_json()
    assert '1' == data['id']
    assert 'Jari' == data['gn']
    assert 'Ojala' == data['fn']

    result = client.delete("/api/name/1")
    assert 200 == result.status_code

    result = client.get("/api/name/")
    assert 200 == result.status_code
    assert [] == result.get_json()
