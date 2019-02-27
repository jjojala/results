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
from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict
import model

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_MODEL_ARG = "model"
_TYPE = "Competitor"

_parser = reqparse.RequestParser()
_parser.add_argument('id', type=str, required=True) # id
_parser.add_argument('eid', type=str, required=True) # event id
_parser.add_argument('nid', type=str, required=True) # name id
_parser.add_argument('cid', type=str, required=False) # community id
_parser.add_argument('start', type=int, required=False) # start time (unix timestamp, ms)
_parser.add_argument('finish', type=int, required=False) # finish time (timestamp)
_parser.add_argument('tags', type=list, location='json', required=False) # tag id refs
_parser.add_argument('status', type=str, required=False) # competitor's status

class Competitors(Resource):
        def make_args(notifications, api, model):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api,
                        _MODEL_ARG: model }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]
                self._model = kwargs[_MODEL_ARG]

        @timeservice.time_service
        def get(self):
                return self._model.list(**request.args.to_dict()), 200

class Competitor(Resource):
        def make_args(notifications, api, model):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api,
                        _MODEL_ARG: model }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]
                self._model = kwargs[_MODEL_ARG]

        @timeservice.time_service
        def get(self, id):
                entity = self._model.get(id)
                if (entity):
                        return entity, 200
                return model.jsonify(model.EntityNotFound(_TYPE, id)), 404

        @timeservice.time_service
        def post(self, id):
                args = _parser.parse_args(strict=True)

                try:
                        entity = self._model.create(args)
                        self._notifications.submit(CREATED, _TYPE, id, entity)
                        return entity, 201, { 'Location': self._api + id }
                except model.IllegalEntity as ex:
                        return model.jsonify(ex), 422
                except (model.EntityAlreadyExists,
                        model.EntityConstraintViolated) as ex:
                        return model.jsonify(ex), 409

        @timeservice.time_service
        def delete(self, id):
                try:
                        self._model.remove(id)
                        self._notifications.submit(REMOVED, _TYPE, id, None)
                        return id, 200
                except model.EntityNotFound as ex:
                        return model.jsonify(ex), 404
                except model.EntityConstraintViolated as ex:
                        return model.jsonify(ex), 409

        @timeservice.time_service
        def patch(self, id):
                try:
                        diff = request.json
                        entity = self._model.patch(id, diff)
                        self._notifications.submit(PATCHED, _TYPE, id, diff)
                        return entity, 200
                except model.IllegalEntity as ex:
                        return model.jsonify(ex), 422
                except model.EntityConstraintViolated as ex:
                        return model.jsonify(ex), 409
                except model.EntityNotFound as ex:
                        return model.jsonify(ex), 404
