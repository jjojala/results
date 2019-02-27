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

class CompetitorModel:
    TYPE = "Competitor"
    
    def __init__(self, controller):
        self._items = []
        self._controller = controller

    def _create_filter(self, **kwargs):
        f = accept_filter # fallbac, if nothing else
        for key, value in kwargs.items():
            if 'eid' == key:
                f = create_equality_filter('eid', value, f)
            elif 'nid' == key:
                f = create_equality_filter('nid', value, f)
            elif 'cid' == key:
                f = create_equality_filter('cid', value, f)
            elif 'tags' == key:
                # support for multi-value parameter, e.g. tags=t-1,t-2
                for v in value.split(','):
                    f = create_intersection_filter('tags', v, f)
            elif 'status' == key:
                f = create_equality_filter('status', value, f)
            else:
                raise ValueError("Unknown filter {}.".format(key))

            # TODO: 'start', 'finish'?!
        return f
        
    def list(self, **kwargs):
        f = self._create_filter(**kwargs)
        return [ c for c in self._items if f(c) ]

    def get(self, id):
        for i in self._items:
            if (id == i["id"]):
                return i
        return None

    def create(self, item):
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists(TYPE, item["id"])
        self._controller.on_competitor_create(item)
        self._items.append(item)
        return item

    def remove(self, competitor_id):
        for i in range(len(self._items)):
            if (competitor_id == self._items[i]["id"]):
                self._controller.on_competitor_remove(competitor_id)
                del self._items[i]
                return True
        raise EntityNotFound(TYPE, competitor_id)

    def patch(self, competitor_id, diff):
        try:
            for i in range(len(self._items)):
                if (competitor_id == self._items[i]["id"]):
                    patched = patch(self._items[i], diff)
                    self._controller.on_competitor_update(
                        competitor_id, diff)
                    self._items[i] = patched
                    return self._items[i]
            raise EntityNotFound(TYPE, competitor_id)
        except PatchConflict as ex:
            raise EntityConstraintViolated(TYPE, competitor_id, str(ex))
