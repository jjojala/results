# -*- coding: utf-8 -*-

from .test_common import client, app
import pytest

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
