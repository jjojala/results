from flask import request
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict

competitors = [
]

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_TYPE = "Competitor"

class Competitors(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self):
                return competitors, 200

class Competitor(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self, id):
                for i in competitors:
                        if (id == i["id"]):
                                return i, 200
                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("start")
                parser.add_argument("finish")
                parser.add_argument("tags", type=list, location='json')
                parser.add_argument("status")
                args = parser.parse_args()
		
                for i in competitors:
                        if (id == i["id"]):
                                return "{} with id {} already exists".format(
                                        _TYPE, id), 409

                item = {
                        "id": id,
                        "start": args["start"],
                        "finish": args["finish"],
                        "tags": args["tags"],
                        "status": args["status"]
                }
                competitors.append(item)
                self._notifications.submit(CREATED, _TYPE, item)

                return item, 201, { 'Location': self._api + id }

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("start")
                parser.add_argument("finish")
                parser.add_argument("tags", type=list, location='json')
                parser.add_argument("status")
                args = parser.parse_args()
		
                for i in competitors:
                        if (id == i["id"]):
                                i["start"] = args["start"]
                                i["finish"] = args["finish"]
                                i["tags"] = args["tags"]
                                i["status"] = args["status"]
                                self._notifications.submit(UPDATED, _TYPE, i)
                                return i, 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def delete(self, id):
                global competitors
                new = [i for i in competitors if i["id"] != id]
                if (len(new) < len(competitors)):
                        competitors = new
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return "{} is deleted.".format(id), 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def patch(self, id):
                diff = request.json

                # TODO: explicitly lock the item
                for i in competitors:
                        if (id == i["id"]):
                                try:
                                        patched = patch(i, diff)
                                        i["start"] = patched["start"]
                                        i["finish"] = patched["finish"]
                                        i["tags"] = patched["tags"]
                                        i["status"] = patched["status"]
                                        self._notifications.submit(PATCHED, _TYPE, diff)
                                        return i, 200
                                except PatchConflict as ex:
                                        return "Patching {} with id {} failed: {}".format(
                                                _TYPE, id, str(ex)), 409

                return "{} with id {} not found".format(_TYPE, id), 404
