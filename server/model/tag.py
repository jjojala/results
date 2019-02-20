# -*- coding: utf-8 -*-
from .common import *
from util.patch import patch, PatchConflict

_TYPE = "Tag"

class TagModel:
    def __init__(self, controller):
        self._items = []
        self._controller = controller

    def _resolve_descendants(self, group_id):
        """Returns a list tag ids of descendants of the given group.
            The given group is expected to exist (not checked).
            If no descendants found, and empty list is returned."""

        descendants = []
        for tag in self._items:
            if group_id == tag["pid"]:
                descendants.append(tag["id"])
                descendants.extend(self._resolve_descendants(tag["id"]))

        return descendants

    def _resolve_scope(self, tag_id):
        """Returns the scope_id of a tag. If no Tag with the given id
            is not found, or any other Tag within the chain of Tags toward
            the scope is missing, an EntityNotFound is raised."""

        tag = self.get(tag_id)
        if tag == None:
            raise EntityNotFound(_TYPE, tag_id)

        if tag["pid"] == None:
            return tag_id       # this is it!

        return self._resolve_scope(tag["pid"])

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
 
    def create(self, tag):
        if self.get(tag["id"]) != None:
            raise EntityAlreadyExists(_TYPE, tag["id"])

        if tag["pid"] == None: # this is a scope tag
            if tag["refs"] != None:  # not allowed for scopes
                raise IllegalEntity(_TYPE, tag["id"],
                                    "Refs not allowed for scope tags.")

        elif self.get(tag["pid"]) == None:  # not a scope
            raise IllegalEntity(_TYPE, tag["id"],
                                str(EntityNotFound(_TYPE, tag["pid"])))

        if tag["refs"] != None: # check refs
            scope_tag_ids = self._resolve_descendants(
                self._resolve_scope(tag["pid"]))
            for referred_tag_id in tag["refs"]:
                if referred_tag_id not in scope_tag_ids:
                    raise IllegalEntity(_TYPE, tag["id"],
                                        str(EntityNotFound(_TYPE, referred_tag)))

        # got this far so the item must be valid
        self._items.append(tag)
        self._controller.created(_TYPE, tag["id"], tag)
        return tag

    def remove(self, tag_id):
        for i in range(len(self._items)):
            if (tag_id == self._items[i]["id"]):
                group = set(self._resolve_descendants(tag_id)).union({tag_id})
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
