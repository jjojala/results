# -*- coding: utf-8 -*-
from .common import *

_TYPE = "Competitor"

class CompetitorModel:
    def __init__(self):
        self._items = []

    def list(self):
        return self._items

    def get(self, id):
        for i in self._items:
            if (id == i["id"]):
                return i
        return None
 
    def create(self, item):
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists(_TYPE, item["id"])
        self._items.append(item)
        return item

    def update(self, item):
        for i in range(len(self._items)):
            if (item["id"] == self._items[i]["id"]):
                self._items[i] = item
                return item
        raise EntityNotFound(_TYPE, item["id"])

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                del self._items[i]
                return True
        raise EntityNotFound(_TYPE, id)

    def patch(self, id, patcher):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    self._items[i] = patcher(self._items[i])
                    return self._items[i]
            raise EntityNotFound(_TYPE, id)
        except PatchConflict as ex:
            raise EntityConstraintViolated(_TYPE, id, str(ex))
