import pytest
from app import create_app

@pytest.fixture
def app():
    return create_app()

@pytest.fixture
def client(app):
    return app.test_client()

def test_tags_get_none(client):
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
    
