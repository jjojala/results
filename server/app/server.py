# -*- coding: utf-8 -*-
"""
   Copyright 2019 Jari ojala (jari.ojala@iki.fi)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
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

def create_app(test_config=None):
        app = Flask(__name__)
        api = Api(app)
        socketio = SocketIO(app, json=json)

        controller = model.ModelController()

        events = controller.get_event_model()
        tags = controller.get_tag_model()
        communities = controller.get_community_model()
        competitors = controller.get_competitor_model()
        names = controller.get_name_model()

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
                try:
                        f = open('dist/index.html')
                except IOError:
                        abort(404)
                        return
                return f.read()

        @app.route('/<path:path>')
        def catch_all(path):
                try:
                        f = open("dist/" + path)
                except IOError:
                        abort(404)
                        return
                return f.read()

        return app

if __name__ == "__main__":
	socketio.run(app, debug=True)
