from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict

tags = [
]

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_TYPE = "Tag"

class Tags(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self):
                return tags, 200

class Tag(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self, id):
                for i in tags:
                        if (id == i["id"]):
                                return i, 200
                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("tag")
                parser.add_argument("desc")
                args = parser.parse_args()
		
                for i in tags:
                        if (id == i["id"]):
                                return "{} with id {} already exists".format(
                                        _TYPE, id), 409

                item = {
                        "id": id,
                        "tag": args["tag"],
                        "desc": args["desc"]
                }
                tags.append(item)
                self._notifications.submit(CREATED, _TYPE, item)

                return item, 201, { 'Location': self._api + id }

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("tag")
                parser.add_argument("desc")
                args = parser.parse_args()
		
                for i in tags:
                        if (id == i["id"]):
                                i["tag"] = args["tag"]
                                i["desc"] = args["desc"]
                                self._notifications.submit(UPDATED, _TYPE, i)
                                return i, 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def delete(self, id):
                global tags
                new = [i for i in tags if i["id"] != id]
                if (len(new) < len(tags)):
                        tags = new
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return "{} is deleted.".format(id), 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def patch(self, id):
                diff = request.json

                # TODO: explicitly lock the item
                for i in tags:
                        if (id == i["id"]):
                                try:
                                        patched = patch(i, diff)
                                        i["tag"] = patched["tag"]
                                        i["desc"] = patched["desc"]
                                        self._notifications.submit(PATCHED, _TYPE, diff)
                                        return i, 200
                                except PatchConflict as ex:
                                        return "Patching {} with id {} failed: {}".format(
                                                _TYPE, id, str(ex)), 409

                return "{} with id {} not found".format(_TYPE, id), 404
