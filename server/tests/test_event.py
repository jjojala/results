# -*- coding: utf-8 -*-

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
