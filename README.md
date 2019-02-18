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

## About the data model

![Classes](classes.svg)

/Event/ as a concept is no doubt self-explanatory. /Tags/ are /specified/ as
part of /Event/. A single /Tag/ -specification by more than one /Event/, but
when the last refence to an /Tag/ is removed, also the /Tag/ itself shall
be removed. An /Event/ may specify a /Tag/ either directly, or indirectly.
/Event/ specifies a /Tag/ directly, if the /Tag/ is referred in the /Events/
/tags/ attribute. /Tags/ may be nested which takse place if a /Tags/ /pid/
(parent id) refers to anoter /Tag/. A nested /Tag/ of a /Tag/ that is directly
referred by a /Event/ is said to be /indirectly/ referred /Tag/. A parent /Tag/ must have the attribute /grp/ (group) set to True. /Tags/ that are either
directly or indirectly referred by an /Event/ are said to be "in scope of
/Event/". The last concept (?) related to /Tag/ hierarchy is the "ref"
-dependency. It means, that when a /Competitor/ is "tagged" with /Tag/
(let's get back to these later), also the /Tags/ that are "refs" of tagged
/Tag/ will be by default tagged for the /Competitor/. Note, that this is 
only default behaviour, and can be explicitly overridden by the user. A typical
use case of this is case, where the initially tagged /Tag/ represents a
competition class, while the referred /Tag/ represents something that is
common to that class, such as bid numbering scheme, start time (in case of
mass start) or start time calculating scheme.

Still few things about /Tags/: /Tag/ may be a group /Tag/, in which case the
attribute /grp/ is set to True. In this case, there may be other /Tags/ that
belong to that group by referring to the "parent" with /pid/ attribute. A
/Tag/ may be "required" (attribute /req/ is set to True). For groups this
means, that at least one of the group's /Tag/ must be set for the /Competitor/
before the /Competitor/ is valid (e.g. one of the group of /Tags/ must be
chosen as the competition class before proceeding with the /Competitor/
registration). For non-group /Tags/ "required" may indicate e.g. wheter the
competitor has paid the registration fee. The final concept of /Tags/ is
the exclusive /Tag/ groups. This means, that if a /Competitor/ has tagged
with one of the /Tags/ within exclusive tag group, it must not be tagged
with another /Tag/ within the same group. This is usefull for (usualy)
cases, that a /Competitor/ may be registered to a single class within the
/Event/ only.

/Competitor/ represents a person's registration to an /Event/. The life-time
of the /Competitor/ is bound to the life-time of the /Event/. When the 
/Event/ is removed, also the /Competitors/ will be removed. A /Name/
represents the individual person (having a name). Obviously the same
person (i.e. the /Name/) may participate to multiple /Events/. However,
the /Name/ will not be removed when the last /Compeitor/ referring it
is removed, as the it is possible, maybe even likely that the same person
will partcipate to some of the future /Events/. In that case, it is 
handy for the user if the /Name/ is still available, so that it is not
needed to type in again (but just chosen, or auto-completed etc).

Finally, the /Community/ represents the community that the person is
representing in the particular /Event/. /Community/ as an attribute of
/Competitor/ is optional, as the person may not represent anything other
than him- or herself. The /Name/ may also be associated with the most recently
represented /Community/. This helps user to fill in registrations, as the
most recently represented /Community/ may be automatically offered when
filling in the registration form. Of course it is possible for the person
to represent different /Communities/.

## Running it

Once compiled, type:


```
cd webui
python ../server/server.py
```

Then navigate to address `http://localhost:5000`


