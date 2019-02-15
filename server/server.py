# -*- coding: utf-8 -*-

from flask import Flask, abort, json
from flask_restful import Api
from flask_socketio import SocketIO, Namespace
import model
import rest
import util

EVENT_API="/api/event/"
NAME_API="/api/name/"
COMMUNITY_API="/api/community/"
TAG_API="/api/tag/"
COMPETITOR_API="/api/competitor/"
NOTIFICATION_API="/api/notifications/"

app = Flask(__name__)
api = Api(app)
socketio = SocketIO(app, json=json)

controller = model.ModelController("test.db")

events = controller.wrap(model.EventModel(controller))
tags = controller.wrap(model.TagModel(controller))
communities = controller.wrap(model.CommunityModel(controller))
competitors = controller.wrap(model.CompetitorModel(controller))
names = controller.wrap(model.NameModel(controller))

notifications = rest.Notifications('/api/notifications', socketio)
socketio.on_namespace(notifications)

api.add_resource(rest.Event, EVENT_API + "<string:id>",
		resource_class_kwargs=rest.Event.make_args(
                        notifications, EVENT_API, events))
api.add_resource(rest.Events, EVENT_API,
		resource_class_kwargs=rest.Events.make_args(
                        notifications, EVENT_API, events))

api.add_resource(rest.Names, NAME_API,
		resource_class_kwargs=rest.Names.make_args(
                        notifications, NAME_API, names))
api.add_resource(rest.Name, NAME_API + "<string:id>",
                 resource_class_kwargs=rest.Name.make_args(
                         notifications, NAME_API, names))

api.add_resource(rest.Communities, COMMUNITY_API,
		resource_class_kwargs=rest.Communities.make_args(
                        notifications, COMMUNITY_API, communities))
api.add_resource(rest.Community, COMMUNITY_API + "<string:id>",
		resource_class_kwargs=rest.Community.make_args(
                        notifications, COMMUNITY_API, communities))

api.add_resource(rest.Tags, TAG_API,
                 resource_class_kwargs=rest.Tags.make_args(
                         notifications, TAG_API, tags))
api.add_resource(rest.Tag, TAG_API + "<string:id>",
                 resource_class_kwargs=rest.Tags.make_args(
                         notifications, TAG_API, tags))

api.add_resource(rest.Competitors, COMPETITOR_API,
                 resource_class_kwargs=rest.Competitors.make_args(
                         notifications, COMPETITOR_API, competitors))
api.add_resource(rest.Competitor, COMPETITOR_API + "<string:id>",
                 resource_class_kwargs=rest.Competitor.make_args(
                         notifications, COMPETITOR_API, competitors))

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
