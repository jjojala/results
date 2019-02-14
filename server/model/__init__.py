import sqlite3

class ItemAlreadyExists(Exception):
    pass

class EventModel:

    def __init__(self):
        self._items = []

    def load(self):
        return self._items

    def find(self, f):
        result = []
        for i in self._items:
            if (f(i))
                result.append(i)

        return result

    def get(self, id):
        for i in self._items:
            if (id == i["id"]):
                return i
 
    def create(self, item):
        for i in self._items:
            if (id == i["id"]):
                raise ItemAlreadyExists()

        self._items.append(item)

