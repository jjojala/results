from flask_restful import Resource, reqparse
import rest.timeservice as timeservice

events = [
]

TYPE = "Event"

class Events(Resource):
	def __init__(self, **kwargs):
		pass

	@timeservice.time_service
	def get(self):
		return events, 200

class Event(Resource):
	def __init__(self, **kwargs):
		self._notifications = kwargs['notifications']

	@timeservice.time_service
	def get(self, id):
		for e in events:
			if (id == e["id"]):
				return e, 200
		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def post(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("date")
		parser.add_argument("name")
		args = parser.parse_args()
		
		for e in events:
			if (id == e["id"]):
				return "{} with id {} already exists".format(TYPE, id), 409

		event = {
			"id": id,
			"date": args["date"],
			"name": args["name"]
		}
		events.append(event)
		self._notifications.submit('CREATED', TYPE, event)

		return event, 201

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
				self._notifications.submit('UPDATED', TYPE, e)
				return e, 200

		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def delete(self, id):
		global events
		newEvents = [e for e in events if e["id"] != id]
		if (len(newEvents) < len(events)):
			events = newEvents
			self._notifications.submit('REMOVED', TYPE, id)
			return "{} is deleted.".format(id), 200
		
		return "{} with id {} not found".format(TYPE, id), 404

	@timeservice.time_service
	def patch(self, id):
		pass # TODO: for now...