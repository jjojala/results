from flask import Flask, abort, json
from flask_restful import Api
from flask_socketio import SocketIO, Namespace
import rest
import util

EVENT_API="/api/event/"
NAME_API="/api/name/"
COMMUNITY_API="/api/community/"
NOTIFICATION_API="/api/notifications/"

app = Flask(__name__)
api = Api(app)
socketio = SocketIO(app, json=json)

notifications = rest.Notifications('/api/notifications', socketio)

socketio.on_namespace(notifications)
api.add_resource(rest.Event, EVENT_API + "<string:id>",
		resource_class_kwargs={
                        "notifications": notifications,
                        "api": EVENT_API })                     
api.add_resource(rest.Events, EVENT_API,
		resource_class_kwargs={
                        "notifications": notifications,
                        "api": EVENT_API })
api.add_resource(rest.Names, NAME_API,
		resource_class_kwargs=rest.Names.makeArgs(
                        notifications, NAME_API))
api.add_resource(rest.Name, NAME_API + "<string:id>",
                 resource_class_kwargs=rest.Name.makeArgs(
                         notifications, NAME_API))
api.add_resource(rest.Communities, COMMUNITY_API,
		resource_class_kwargs=rest.Communities.makeArgs(
                        notifications, COMMUNITY_API))
api.add_resource(rest.Community, COMMUNITY_API + "<string:id>",
		resource_class_kwargs=rest.Community.makeArgs(
                        notifications, COMMUNITY_API))

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
