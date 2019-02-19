# -*- coding: utf-8 -*-
from .common import *
from util.patch import patch, PatchConflict

_TYPE = "Tag"

class TagModel:
    def __init__(self, controller):
        self._items = []
        self._controller = controller

    def _check_refs(self, root_id, refs):
        """Raises an exception, if the given root_id refers to either
            a non-existing tag or if the references would constitute
            a circular dependency. If neither is true, function pass."""
        
        if refs == None:
            return
        
        for referred_id in refs:
            if reference_id == root_id:
                raise EntityConstraintViolated(_TYPE, referee_id,
                    "Tag is constituting a curcular reference.")

                referred_tag = self.get(referred_id)
                if referred_tag == None:
                    raise EntityConstraintViolated(_TYPE, item["id"],
                        "Reference {} with id {} not found.".format(
                            _TYPE, r))

                this._check_refs(root_id, referred_tag["refs"])

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
 
    def create(self, item):
        self._check_parent(item)
        self._check_refs(item["id"], item["refs"])
        # TODO: that we're still working within the scope of event!
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists(_TYPE, item["id"])

        self._items.append(item)
        self._controller.created(_TYPE, item["id"], item)
        return item

# TODO: To be removed!
#    def update(self, item):
#        """TODO: To be removed?! """
#        for i in range(len(self._items)):
#            if (item["id"] == self._items[i]["id"]):
#                self._controller.updated(_TYPE, item["id"], self._items[i], item)
#                self._items[i] = item
#                return item
#        raise EntityNotFound(_TYPE, item["id"])

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
