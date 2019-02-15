from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
import model

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_MODEL_ARG = "model"
_TYPE = "Name"

_parser = reqparse.RequestParser()
_parser.add_argument('id', type=str, required=True) # id
_parser.add_argument('gn', type=str, required=True) # given name
_parser.add_argument('fn', type=str, required=True) # family name
_parser.add_argument('rc', type=str, required=False) # most recent community

class Names(Resource):
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
                return self._model.list(), 200

class Name(Resource):
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
