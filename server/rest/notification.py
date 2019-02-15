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
