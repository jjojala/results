# -*- coding: utf-8 -*-

from .test_common import client, app
import pytest

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
    
