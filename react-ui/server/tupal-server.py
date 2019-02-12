from flask import Flask, abort, json
from flask_restful import Api
from flask_socketio import SocketIO, Namespace
import rest
import util

app = Flask(__name__)
api = Api(app)
socketio = SocketIO(app, json=json)

notifications = rest.Notifications('/api/notifications', socketio)
resource_config = {
	'notifications': notifications
}

socketio.on_namespace(notifications)
api.add_resource(rest.Event, "/api/event/<string:id>",
		resource_class_kwargs=resource_config)
api.add_resource(rest.Events, "/api/event/",
		resource_class_kwargs=resource_config)
api.add_resource(rest.Names, "/api/name/",
		resource_class_kwargs=resource_config)
api.add_resource(rest.Name, "/api/name/<string:id>",
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
