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
