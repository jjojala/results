from flask_restful import Resource, reqparse

names = [
]

TYPE = "Name"

class Names(Resource):
	def __init__(self, **kwargs):
		pass

	def get(self):
		return names, 200

class Name(Resource):
	def __init__(self, **kwargs):
		self._notifications = kwargs['notifications']

	def get(self, id):
		for n in names:
			if (id == n["id"]):
				return n, 200
		return "{} with id {} not found".format(TYPE, id), 404

	def post(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("gn")
		parser.add_argument("fn")
		parser.add_argument("rc")
		args = parser.parse_args()
		
		for n in names:
			if (id == n["id"]):
				return "{} with id {} already exists".format(TYPE, id), 409
			
		name = {
			"id": id,
			"gn": args["gn"],
			"fn": args["fn"],
			"rc": args["rc"]
		}
		names.append(name)
		self._notifications.submit('CREATED', TYPE, name)

		return name, 201

	def put(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("gn")
		parser.add_argument("fn")
		parser.add_argument("rc")
		args = parser.parse_args()
		
		for n in names:
			if (id == e["id"]):
				e["gn"] = args["gn"]
				e["fn"] = args["fn"]
				e["rc"] = args["rc"]
				
				self._notifications.submit('UPDATED', TYPE, n)
				return n, 200

		return "{} with id {} not found".format(TYPE, id), 404

	def delete(self, id):
		global names
		newNames = [n for n in names if n["id"] != id]
		if (len(newNames) < len(names)):
			names = newNames
			self._notifications.submit('REMOVED', TYPE, id)
			return "{} with id {} is deleted.".format(TYPE, id), 200
		
		return "{} with id {} not found".format(TYPE, id), 404
