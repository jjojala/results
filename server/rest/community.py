from flask_restful import Resource, reqparse
from .notification import CREATED, UPDATED, PATCHED, REMOVED
import rest.timeservice as timeservice

communities = [
]

TYPE = "Community"

class Communities(Resource):
	def __init__(self, **kwargs):
		pass

	@timeservice.time_service
	def get(self):
		return communities, 200

class Community(Resource):
	def __init__(self, **kwargs):
		self._notifications = kwargs['notifications']

	@timeservice.time_service
	def get(self, id):
		for i in communities:
			if (id == i["id"]):
				return i, 200
		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def post(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("name")
		parser.add_argument("abbr")
		args = parser.parse_args()
		
		for i in communities:
			if (id == i["id"]):
				return "{} with id {} already exists".format(TYPE, id), 409

		community = {
			"id": id,
			"name": args["name"],
			"abbr": args["abbr"]
		}
		communities.append(community)
		self._notifications.submit(CREATED, TYPE, community)

		return community, 201

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
				self._notifications.submit(UPDATED, TYPE, i)
				return i, 200

		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def delete(self, id):
		global communities
		new = [i for i in communities if i["id"] != id]
		if (len(new) < len(communities)):
			communities = new
			self._notifications.submit(REMOVED, TYPE, id)
			return "{} is deleted.".format(id), 200
		
		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def patch(self, id):
		pass # TODO: for now...