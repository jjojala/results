# -*- coding: utf-8 -*-

from .test_common import client, app
import pytest

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
