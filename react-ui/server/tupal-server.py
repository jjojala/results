from flask import Flask, abort, json
from flask_restful import Api
from flask_socketio import SocketIO, Namespace
from notification import Notifications
from patch import PatchConflict, diff, patch
from event import events, Event, Events
from name import names, Name, Names

app = Flask(__name__)
api = Api(app)
socketio = SocketIO(app, json=json)

notifications = Notifications('/api/notifications', socketio)
resource_config = {
	'notifications': notifications
}

socketio.on_namespace(notifications)
api.add_resource(Event, "/api/event/<string:id>",
		resource_class_kwargs=resource_config)
api.add_resource(Events, "/api/event/",
		resource_class_kwargs=resource_config)
api.add_resource(Names, "/api/name/",
		resource_class_kwargs=resource_config)
api.add_resource(Name, "/api/name/<string:id>",
		resource_class_kwargs=resource_config)

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
	socketio.run(app, debug=True)
