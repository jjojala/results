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
_TYPE = "Event"

class Events(Resource):
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

class Event(Resource):
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
                return model.EntityNotFound.str(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("id")
                parser.add_argument("date")
                parser.add_argument("name")
                args = parser.parse_args()

                try:
                        entity = self._model.create(args)
                        self._notifications.submit(CREATED, _TYPE, entity)
                        return entity, 201, { 'Location': self._api + id }
                except model.EntityAlreadyExists as ex:
                        return str(ex), 409

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("id")
                parser.add_argument("date")
                parser.add_argument("name")
                args = parser.parse_args()

                try:
                        entity = self._model.update(args)
                        self._notifications.submit(UPDATED, _TYPE, entity)
                        return entity, 200

                except model.EntityNotFound as ex:
                        return str(ex), 404

        @timeservice.time_service
        def delete(self, id):
                try:
                        self._model.remove(id)
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return id, 200

                except model.EntityNotFound as ex:
                        return str(ex), 404

        @timeservice.time_service
        def patch(self, id):
                diff = request.json
                def patcher(entity):
                        return patch(entity, diff)

                try:
                        entity = self._model.patch(id, patcher)
                        self._notifications.submit(PATCHED, _TYPE, diff)
                        return entity, 200

                except model.EntityConstraintViolated as ex:
                        return str(ex), 409

                except model.EntityNotFound as ex:
                        return str(ex), 404
