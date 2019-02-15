# -*- coding: utf-8 -*-

from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict
import model

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_MODEL_ARG = "model"
_TYPE = "Tag"

_parser = reqparse.RequestParser()
_parser.add_argument('id', type=str, required=True) # id
_parser.add_argument('pid', type=str, required=False) # parent's id 
_parser.add_argument('tag', type=str, required=True) # tag
_parser.add_argument('desc', type=str, required=True) # description
_parser.add_argument('grp', type=bool, required=False) # if this is tag group
# if group's values are exclusive
_parser.add_argument('excl', type=bool, required=False)
_parser.add_argument('req', type=bool, required=False) # if value is mandatory
# setting this tag non-obligatorily sets the 'ref' as well
_parser.add_argument('ref', type=str, required=False)

class Tags(Resource):
        def makeArgs(notifications, api, model):
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
                return self._model.list(), 200

class Tag(Resource):
        def makeArgs(notifications, api, model):
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
                except model.EntityAlreadyExists as ex:
                        return model.jsonify(ex), 409

        @timeservice.time_service
        def put(self, id):
                args = _parser.parse_args(strict=True)

                try:
                        entity = self._model.update(args)
                        self._notifications.submit(UPDATED, _TYPE, id, entity)
                        return entity, 200
                except model.EntityNotFound as ex:
                        return model.jsonify(ex), 404

        @timeservice.time_service
        def delete(self, id):
                try:
                        self._model.remove(id)
                        self._notifications.submit(REMOVED, _TYPE, id, None)
                        return id, 200
                except model.EntityNotFound as ex:
                        return model.jsonify(ex), 404

        @timeservice.time_service
        def patch(self, id):
                try:
                        diff = request.json
                        entity = self._model.patch(id, diff)
                        self._notifications.submit(PATCHED, _TYPE, id, diff)
                        return entity, 200
                except model.EntityConstraintViolated as ex:
                        return model.jsonify(ex), 409
                except model.EntityNotFound as ex:
                        return model.jsonify(ex), 404