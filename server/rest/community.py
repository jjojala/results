from flask import request, jsonify
from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice
from util.patch import patch, PatchConflict
import sys

communities = [
]

_NOTIFICATION_ARG = "notifications"
_API_ARG = "api"
_TYPE = "Community"

class Communities(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self):
                return communities, 200

class Community(Resource):
        def makeArgs(notifications, api):
                return {
                        _NOTIFICATION_ARG: notifications,
                        _API_ARG: api }

        def __init__(self, **kwargs):
                self._notifications = kwargs[_NOTIFICATION_ARG]
                self._api = kwargs[_API_ARG]

        @timeservice.time_service
        def get(self, id):
                for i in communities:
                        if (id == i["id"]):
                                return i, 200
                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def post(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("name")
                parser.add_argument("abbr")
                args = parser.parse_args()
		
                for i in communities:
                        if (id == i["id"]):
                                return "{} with id {} already exists".format(
                                        _TYPE, id), 409

                community = {
                        "id": id,
                        "name": args["name"],
                        "abbr": args["abbr"]
                }
                communities.append(community)
                self._notifications.submit(CREATED, _TYPE, community)

                return community, 201, { 'Location': self._api + id }

        @timeservice.time_service
        def put(self, id):
                parser = reqparse.RequestParser()
                parser.add_argument("name")
                parser.add_argument("abbr")
                args = parser.parse_args()
		
                for i in communities:
                        if (id == i["id"]):
                                i["name"] = args["name"]
                                i["abbr"] = args["abbr"]
                                self._notifications.submit(UPDATED, _TYPE, i)
                                return i, 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def delete(self, id):
                global communities
                new = [i for i in communities if i["id"] != id]
                if (len(new) < len(communities)):
                        communities = new
                        self._notifications.submit(REMOVED, _TYPE, id)
                        return "{} is deleted.".format(id), 200

                return "{} with id {} not found".format(_TYPE, id), 404

        @timeservice.time_service
        def patch(self, id):
                args = request.json

                # TODO: explicitly lock the item
                for i in communities:
                        if (id == i["id"]):
                                try:
                                        patched = patch(i, args)
                                        i["name"] = patched["name"]
                                        i["abbr"] = patched["abbr"]
                                        return i, 200
                                except PatchConflict as ex:
                                        return "Patching {} with id {} failed: {}".format(
                                                _TYPE, id, str(ex)), 409

                return "{} with id {} not found".format(_TYPE, id), 404
