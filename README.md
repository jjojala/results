# results - web-based timing and scoring software for ski- and running races

Completely re-written, but still ever-lasting...

UI is now react-based, backend on the other hand written by python3. UI is still based on AJAX/REST API, while the notifications will be submitted through SocketIO (fundamentally either by long-polling, or preferably via websocket) by using home-made *RCNP* (Resource Change Notification Protocol). Additionally HTTP PATCH will be supported for partial entity updates (this has also introduced new verb 'PATCH' to RCNP).

## The backend

The backend requires bunch of libraies such as [Flask](http://flask.pocoo.org/) for serving static web files, [Flask-RESTful](https://flask-restful.readthedocs.io/) for REST, and [Flask-SocketIO](https://flask-socketio.readthedocs.io/) for SocketIO implementation required for *RCNP*. The database will be most probably implemented with sqlite (let's see). All in all the dependencies will be maintained in the code itself while using some python package manager to resolve them (TBD).

## The frontend

The frontend is based on [React](https://reactjs.org/), and the state management is following [MEIOSIS](https://meiosis.js.org) -pattern by utilizing [flyd](https://github.com/paldepind/flyd) and [Patchinko](https://github.com/barneycarroll/patchinko). I did some investigation of commonly used state management libraries such as Redux and Mobx, but it quickly seemed far too tedious to get grip of them, as I'm still a bit newbie with js. Besides, doing it on my own way sound much more fun! 

Of course the app has lot more dependencies, but they're all included in the [package.json](webui/webpack.json) -file. The building is based on webpack, by typing:


```
cd webui
npx webpack --mode production
```

## Running it

Once compiled, type:


```
cd webui
python ../server/tupal-server.py
```

Then navigate to address `http://localhost:5000`


