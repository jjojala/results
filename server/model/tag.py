# -*- coding: utf-8 -*-
"""
   Copyright 2019 Jari ojala (jari.ojala@iki.fi)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
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

    def _update_refs(self, removed_tag_ids):
        """Updates tag refs according to removal of given tags."""

        for tag in self._items:
            if tag["refs"] != None:
                updated_refs = [ ref_id for ref_id in tag["refs"]
                  if ref_id not in removed_tag_ids ]
                if len(tag["refs"]) != len(updated_refs):
                    self._controller.on_tag_update(tag['id'], {
                        'refs': [ tag['refs'].copy(), updated_refs ] })
                    tag["refs"] = updated_refs

    def _remove_one(self, id):
        for i in range(len(self._items)):
            if self._items[i]["id"] == id:
                self._controller.on_tag_remove(id)
                del self._items[i]
                return True
        return False

    def _remove_group(self, group):
        count = 0
        for i in group:
            if self._remove_one(i):
                count = count + 1
        return count
          
    def list(self, **kwargs):
        scope = self._items
        if 'ts_id' in kwargs:
            root_id = kwargs['ts_id']
            scope = self._resolve_descendants(root_id) + [ root_id ]
            return [ tag for tag in self._items if tag['id'] in scope ]
        return scope

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
        self._controller.on_tag_create(tag)
        self._items.append(tag)
        return tag

    def remove(self, tag_id):
        for i in range(len(self._items)):
            if (tag_id == self._items[i]["id"]):
                to_be_removed = self._resolve_descendants(tag_id) + [ tag_id ]
                self._update_refs(to_be_removed)                
                self._remove_group(to_be_removed)
                return True
        raise EntityNotFound(_TYPE, id)

    def patch(self, id, diff):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    patched = patch(self._items[i], diff)
                    new_refs = [ ref_id for ref_id in patched["refs"]
                                 if ref_id not in self._items[i]["refs"] ]
                    if len(new_refs) > 0:
                        scope_tag_ids = self._resolve_descendants(
                            self._resolve_scope(id))
                        for ref_id in new_refs:
                            if ref_id not in scope_tag_ids:
                                raise IllegalEntity(_TYPE, id,
                                                    str(EntityNotFound(_TYPE, ref_id)))

                    if patched["pid"] != self._items[i]["pid"]:
                        if patched["pid"] == None: # this is a scope tag
                            if patched["refs"] != None:  # .. then refs not allowed
                                raise IllegalEntity(_TYPE, patched["id"],
                                                    "Refs not allowed for scope tags.")

                        elif self.get(patched["pid"]) == None:  # not a scope
                            raise IllegalEntity(_TYPE, id,
                                                str(EntityNotFound(_TYPE, patched["pid"])))

                    self._controller.on_tag_update(id, diff)
                    self._items[i] = patched
                    return self._items[i]
            raise EntityNotFound(_TYPE, id)
        except PatchConflict as ex:
            raise EntityConstraintViolated(_TYPE, id, str(ex))
