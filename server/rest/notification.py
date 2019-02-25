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
from flask_socketio import SocketIO, Namespace
import uuid

CREATED = 'CREATED'
UPDATED = 'UPDATED'
PATCHED = 'PATCHED'
REMOVED = 'REMOVED'


class Notifications(Namespace):
	def __init__(self, namespace, socketio):
		super(Notifications, self).__init__(namespace)
		self._socketio = socketio
		self._nodeId = str(uuid.uuid4())

	def submit(self, event, entityType, entityId, data):
		notification = {
			'event': event + ' ' + entityType + ' ' + entityId + ' ' + self._nodeId,
			'data': data
		}
		self._socketio.emit('notification', notification, namespace='/api/notifications')

	def on_connect(self):
		print("on_connect()")
	
	def on_disconnect(self):
		print("on_disconnect()")

	def on_notification(self, data):
		print("on_notification(data={})".format(data))
