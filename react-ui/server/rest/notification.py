from flask_socketio import SocketIO, Namespace

class Notifications(Namespace):
	def __init__(self, namespace, socketio):
		super(Notifications, self).__init__(namespace)
		self._socketio = socketio

	def submit(self, event, entity, data):
		notification = {
			'event': event + ' ' + entity,
			'data': data
		}
		self._socketio.emit('notification', notification, namespace='/api/notifications')

	def on_connect(self):
		print("on_connect()")
	
	def on_disconnect(self):
		print("on_disconnect()")

	def on_notification(self, data):
		print("on_notification(data={})".format(data))