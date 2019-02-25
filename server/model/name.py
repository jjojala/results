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
        return item

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                del self._items[i]
                return True
        raise EntityNotFound(_TYPE, id)

    def patch(self, id, diff):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    patched = patch(self._items[i], diff)
                    self._items[i] = patched
                    return self._items[i]
            raise EntityNotFound(_TYPE, id)
        except PatchConflict as ex:
            raise EntityConstraintViolated(_TYPE, id, str(ex))
