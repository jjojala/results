from .test_server import client, app
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
