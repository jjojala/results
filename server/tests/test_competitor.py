# -*- coding: utf-8 -*-

from .test_common import client, app
import pytest

def test_get_competitors(client):
    """Get all competitors."""

    result = client.get("/api/competitor/")
    assert 200 == result.status_code
    assert [] == result.get_json()

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
