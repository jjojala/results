from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict

events = [
]

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_TYPE = "Event"

class Events(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self):
                return events, 200

class Event(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self, id):
                for e in events:
                        if (id == e["id"]):
                                return e, 200
                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("date")
                parser.add_argument("name")
                args = parser.parse_args()

                for e in events:
                        if (id == e["id"]):
                                return "{} with id {} already exists".format(
                                        _TYPE, id), 409

                event = {
                        "id": id,
                        "date": args["date"],
                        "name": args["name"]
                }
                events.append(event)
                self._notifications.submit(CREATED, _TYPE, event)

                return event, 201, { 'Location': self._api + id }

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("date")
                parser.add_argument("name")
                args = parser.parse_args()

                for e in events:
                        if (id == e["id"]):
                                e["date"] = args["date"]
                                e["name"] = args["name"]
                                self._notifications.submit(UPDATED, _TYPE, e)
                                return e, 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def delete(self, id):
                global events
                newEvents = [e for e in events if e["id"] != id]
                if (len(newEvents) < len(events)):
                        events = newEvents
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return "{} is deleted.".format(id), 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def patch(self, id):
                diff = request.json

                # TODO: Explicitly lock the items
                for i in events:
                        if (id == i["id"]):
                                try:
                                        patched = patch(i, diff)
                                        i["date"] = patched["date"]
                                        i["name"] = patched["name"]
                                        self._notifications.submit(
                                                PATCHED, _TYPE, diff)
                                        return i, 200
                                except PatchConflict as ex:
                                        return "Patching {} with id {} failed: {}".format(
                                                _TYPE, id, str(ex)), 409

                return "{} with id {} not found".format(_TYPE, id), 404
