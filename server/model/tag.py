# -*- coding: utf-8 -*-
from .common import *
from util.patch import patch, PatchConflict

_TYPE = "Tag"

class TagModel:
    def __init__(self, controller):
        self._items = []
        self._controller = controller

    def _check_refs(self, scope, root_id, refs):
        """Raises an exception, if the given root_id refers to either
            a non-existing tag or if the references would constitute
            a circular dependency. If neither is true, function pass."""
        
        if refs == None:
            return
        
        for referred_id in refs:
            if referred_id == root_id:
                raise EntityConstraintViolated(_TYPE, root_id,
                    "Tag is constituting a curcular reference.")

                if referred_id not in scope:
                    raise EntityConstraintViolated(_TYPE, root_id,
                        "Tag is referring to Tag {} outside of it's scope.".format(
                            reference_id))
                
                referred_tag = self.get(referred_id)
                if referred_tag == None:
                    raise EntityConstraintViolated(_TYPE, root_id,
                        "Reference {} with id {} not found.".format(
                            _TYPE, referred_id))

                this._check_refs(scope, root_id, referred_tag["refs"])

    def _check_parent(self, item):
        if item["pid"] == None:
            return

        parent = self.get(item["pid"])
        if parent == None:
            raise EntityConstraintViolated(_TYPE, item["id"],
                "Parent {} with id {} not found.".format(
                    _TYPE, pid))

        if parent["grp"] == None or parent["grp"] == False:
            raise EntityConstraintViolated(_TYPE, pid,
                "Expected to be a group.")

    def _get_children(self, parent):
        if parent["grp"] == None or parent["grp"] == False:
            return set()
        
        children = set()
        for i in self._items:
            if parent["id"] == i["pid"]:
                children.add(i["id"])
                children.update(self._get_children(i["id"]))

        return children

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
 
    def create(self, item, event_id=None):
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists(_TYPE, item["id"])

        scope = self._controller.get_event_tags(event_id)
        if scope == None:
            scope = [ i["id"] for i in self._items ]
            
        self._check_parent(item)
        if item["refs"] != None:
            scope = self._controller.get_event_tags(event_id)
            if scope == None:
                scope = [ i["id"] for i in self._items ]

            self._check_refs(scope, item["id"], item["refs"])

        self._items.append(item)
        self._controller.created(_TYPE, item["id"], item)
        return item

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                group = set({id}).union(self._get_children(self._items[i]))
                self._controller.on_pre_remove(_TYPE, group)
                self._remove_set(group)
                return True
        raise EntityNotFound(_TYPE, id)

    def patch(self, id, diff):
        # TODO: check refs, check groups
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
