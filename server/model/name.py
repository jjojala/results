# -*- coding: utf-8 -*-
from .common import *
from util.patch import patch, PatchConflict

_TYPE = "Name"

class NameModel:
    def __init__(self, controller):
        self._items = []
        self._controller = controller

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
        self._controller.created(_TYPE, item["id"], item)
        return item

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                self._controller.removed(_TYPE, id, self._items[i])
                del self._items[i]
                return True
        raise EntityNotFound(_TYPE, id)

    def patch(self, id, diff):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    patched = patch(self._items[i], diff)
                    self._controller.patched(_TYPE, id, diff, self._items[i], patched)
                    self._items[i] = patched
                    return self._items[i]
            raise EntityNotFound(_TYPE, id)
        except PatchConflict as ex:
            raise EntityConstraintViolated(_TYPE, id, str(ex))
