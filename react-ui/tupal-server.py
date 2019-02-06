from flask import Flask, abort
from flask_restful import Api, Resource, reqparse

app = Flask(__name__)
api = Api(app)

events = [
]

class Events(Resource):
	def get(self):
		return events, 200

class Event(Resource):
	def get(self, id):
		for e in events:
			if (id == e["id"]):
				return e, 200
		return "Event not found", 404

	def post(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("date")
		parser.add_argument("name")
		args = parser.parse_args()
		
		for e in events:
			if (id == e["id"]):
				return "Event with id {} already exists".format(id), 400
			
		event = {
			"id": id,
			"date": args["date"],
			"name": args["name"]
		}
		events.append(event)
		return event, 201

	def put(self, id):
		parser = reqparse.RequestParser()
		parser.add_argument("date")
		parser.add_argument("name")
		args = parser.parse_args()
		
		for e in events:
			if (id == e["id"]):
				e["date"] = args["date"]
				e["name"] = args["name"]
				return e, 200

		return "Event not found", 404

	def delete(self, id):
		global events
		events = [e for e in events if e["id"] != id]
		return "{} is deleted.".format(id), 200

api.add_resource(Event, "/api/event/<string:id>")
api.add_resource(Events, "/api/event/")

@app.route('/')
def root():
	if not app.debug:
		abort(404)
	try:
		f = open('dist/index.html')
	except IOError:
		abort(404)
		return
	return f.read()

@app.route('/<path:path>')
def catch_all(path):
	if not app.debug:
		abort(404)
	try:
		f = open("dist/" + path)
	except IOError:
		abort(404)
		return
	return f.read()

if __name__ == "__main__":
	app.run(debug=True)
