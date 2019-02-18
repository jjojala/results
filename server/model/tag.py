# -*- coding: utf-8 -*-
from .common import *
from util.patch import patch, PatchConflict

_TYPE = "Tag"

class TagModel:
    def __init__(self, controller):
        self._items = []
        self._controller = controller

    def _get_dependants(self, id):
        """Return a set of id's of Tags that are children of the Tag
             with the given id. If no dependants found, and empty set
             will be returned."""
        deps = { id }
        for i in self._items:
            if (id == i["pid"] or (i["refs"] and id in i["refs"])):
                deps.add(i["id"])
        return deps

    def _remove_one(self, id):
        for i in range(len(self._items)):
            if self._items[i]["id"] == id:
                del self._items[i]
                return True
        return False

    def _remove_set(self, id_set):
        count = 0
        for i in id_set:
            if self._remove_one(i):
                count = count + 1
        return count

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

    def update(self, item):
        """TODO: To be removed?! """
        for i in range(len(self._items)):
            if (item["id"] == self._items[i]["id"]):
                self._controller.updated(_TYPE, item["id"], self._items[i], item)
                self._items[i] = item
                return item
        raise EntityNotFound(_TYPE, item["id"])

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                deps = self._get_dependants(id)
                self._controller.on_pre_remove(_TYPE, deps)
                self._remove_set(deps)
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
