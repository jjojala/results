# results - web-based timing and scoring software for ski- and running races

Completely re-written, but still ever-lasting...

UI is now react-based, backend on the other hand written by python3. UI is still based on AJAX/REST API, while the notifications will be submitted through SocketIO (fundamentally either by long-polling, or preferably via websocket) by using home-made RCNP (Resource Change Notification Protocol). Additionally HTTP PATCH wiill be supported for partial entity updates (this has also introduced new verb 'PATCH' to RCNP).

