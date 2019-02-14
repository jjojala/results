from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict
import sys

names = [
]

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_TYPE = "Name"

class Names(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self):
                return names, 200

class Name(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self, id):
                for n in names:
                        if (id == n["id"]):
                                return n, 200
                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("gn")
                parser.add_argument("fn")
                parser.add_argument("rc")
                args = parser.parse_args()
		
                for n in names:
                        if (id == n["id"]):
                                return "{} with id {} already exists".format(_TYPE, id), 409

                name = {
                        "id": id,
                        "gn": args["gn"],
                        "fn": args["fn"],
                        "rc": args["rc"]
                }
                names.append(name)
                self._notifications.submit(CREATED, _TYPE, name)

                return name, 201, { 'Location': self._api + id }

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("gn")
                parser.add_argument("fn")
                parser.add_argument("rc")
                args = parser.parse_args()

                for n in names:
                        if (id == n["id"]):
                                n["gn"] = args["gn"]
                                n["fn"] = args["fn"]
                                n["rc"] = args["rc"]

                                self._notifications.submit(UPDATED, _TYPE, n)
                                return n, 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def delete(self, id):
                global names
                newNames = [n for n in names if n["id"] != id]
                if (len(newNames) < len(names)):
                        names = newNames
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return "{} with id {} is deleted.".format(_TYPE, id), 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def patch(self, id):
                diff = request.json

                # TODO: Explicitly lock the items
                for i in names:
                        if (id == i["id"]):
                                try:
                                        patched = patch(i, diff)
                                        i["gn"] = patched["gn"]
                                        i["fn"] = patched["fn"]
                                        i["rc"] = patched["rc"]

                                        self._notifications.submit(
                                                PATCHED, _TYPE, diff)
                                        return i, 200
                                except PatchConflict as ex:
                                        return "Patching {} with id {} failed: {}".format(
                                                _TYPE, id, str(ex)), 409

                return "{} with id {} not found".format(_TYPE, id), 404
