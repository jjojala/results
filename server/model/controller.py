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
import model
from util.patch import diff

class ModelWrapper:
    def __init__(self, controller, wrapped):
        self._controller = controller
        self._wrapped = wrapped

    def __getattr__(self, member_name):
        member = self._wrapped.__getattribute__(member_name)
        if callable(member):
            def decorator(*args, **kwargs):
                self.setup()
                try:
                    result = member(*args, **kwargs)
                    if (result == self._wrapped):
                        return self
                    self.success()
                    return result
                except:
                    self.error()
                    raise
            return decorator
        else:
            return member

    def setup(self):
        print("setup()") 
        pass

    def success(self):
        #self._controller.get_connection().commit()
        print("success()")

    def error(self):
        print("error()")
        #self._controller.get_connection().rollback()

class ModelController:
    def __init__(self, **kwargs):
        """Initialize ModelController. Supported args (TBD)."""
        self._community_model = model.CommunityModel(self)
        self._name_model = model.NameModel(self)
        self._tag_model = model.TagModel(self)
        self._event_model = model.EventModel(self)
        self._competitor_model = model.CompetitorModel(self)

    def get_community_model(self):
        return ModelWrapper(self, self._community_model)

    def get_name_model(self):
        return ModelWrapper(self, self._name_model)

    def get_tag_model(self):
        return ModelWrapper(self, self._tag_model)

    def get_event_model(self):
        return ModelWrapper(self, self._event_model)

    def get_competitor_model(self):
        return ModelWrapper(self, self._competitor_model)
    
    def on_tag_create(self, tag):
        print("on_tag_create(tag={})".format(tag))

    def on_tag_update(self, tag_id, diff):
        print("on_tag_update(tag_id={}, diff={})".format(tag_id, diff))

    def on_tag_remove(self, tag_id):
        print("on_tag_remove(tag_id={})".format(tag_id))
        referring_events = self._event_model.list(ts_id=tag_id)
        print("\treferring_events={}".format(referring_events))

    def on_name_create(self, name):
        print("on_name_create(name={})".format(name))
        try:
            self._check_community(name['rc'])
        except Exception as ex:
            raise IllegalEntity(model.NameModel.TYPE, name['id'],
                                str(ex))

    def on_name_update(self, name_id, diff):
        print("on_name_update(name_id={}, diff={})".format(name_id, diff))

    def on_name_remove(self, name_id):
        print("on_name_remove(name_id={})".format(name_id))

    def on_event_create(self, event):
        print("on_event_create(event={})".format(event))
        try:
            self._check_tag_scope_id(event['ts_id'])
        except Exception as ex:
            raise IllegalEntity(model.EventModel.TYPE, event['id'],
                                str(ex))
    
    def on_event_update(self, event_id, diff):
        print("on_event_update(event_id={}, diff={})".format(event_id, diff))

    def on_event_remove(self, event_id):
        print("on_event_remove(event_id={})".format(event_id))

    def _check_tag_scope_id(self, ts_id):
        if ts_id != None:
            tag_scope = self._tag_model.get(ts_id)
            if tag_scope == None:
                raise EntityNotFound(model.TagModel.TYPE, ts_id)

            if tag_scope['pid'] != None:
                raise IllegalEntity(model.TagModel.TYPE, ts_id,
                                    "Tag scope shall not have pid.")

            if tag_scope['grp'] == None or tag_scope['grp'] == False:
                raise IllegalEntity(model.TagModel.TYPE, ts_id,
                                    "Tag scope shall be a group.")

    def _get_event(self, event_id):
        if event_id == None:
            raise ValueError("Reference to Event undefined!")

        event = self._event_model.get(event_id)
        if event == None:
            raise EntityNotFound(model.EventModel.TYPE, event_id)

        return event

    def _get_scope_tag_ids(self, event):
        if event['ts_id'] != None:
            return [ t['id'] for t
                     in self._tag_model.list(ts_id=event['ts_id']) ]
        
    def _check_tags_in_scope(self, tag_ids, scope_ids):
        if tag_ids != None:
            for tag_ref in tag_ids:
                if tag_ref not in scope_ids:
                    raise ValueError('Reference to Tag {} is stale.'.format(
                        tag_ref))

    def _check_name(self, name_id):
        if name_id == None:
            raise ValueError("Reference to Name is undefined.")

        if self._name_model.get(name_id) == None:
            raise EntityNotFound(model.NameModel.TYPE, name_id)

    def _check_community(self, community_id):
        if community_id != None:
            if self._community_model.get(community_id) == None:
                raise EntityNotFound(model.CommunityModel.TYPE, community_id)

    def on_competitor_create(self, competitor):
        print("on_competitor_create(competitor={})".format(competitor))
        try:
            self._check_tags_in_scope(competitor['tags'],
                                      self._get_scope_tag_ids(
                                          self._get_event(competitor['eid'])))
            self._check_name(competitor['nid'])
            self._check_community(competitor['cid'])
        except Exception as ex:
            raise IllegalEntity(model.CompetitorModel.TYPE, competitor['id'],
                                str(ex))

    def on_competitor_update(self, competitor_id, diff):
        print("on_competitor_update(competitor_id={}, diff={})".format(
            competitor_id, diff))

    def on_competitor_remove(self, competitor_id):
        print("on_competitor_remove(competitor_id={})".format(competitor_id))

    def on_community_create(self, community):
        print("on_community_create(community)".format(community))

    def on_community_update(self, community_id, diff):
        print("on_community_update(community_id={}, diff={})".format(
            community_id, diff))

    def on_community_remove(self, community_id):
        print("on_community_remove(community_id={})".format(community_id))
        if len(self._competitor_model.list(cid=community_id)) > 0:
            raise EntityConstraintViolated(model.CommunityModel.TYPE,
                                           community_id,
                                           "Removal denied as Community is referred by at least one Competitor")
        
        for n in self._name_model.list(rc=community_id):
            updated = n.copy()
            updated['rc'] = None            
            self._name_model.patch(n['id'], diff(n, updated))
