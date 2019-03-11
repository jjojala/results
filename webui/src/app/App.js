import './App.css'
import React, { Component } from "react";
import { render } from "react-dom";
import { BrowserRouter as Router, Route } from "react-router-dom";
import flyd from "flyd";
import { P } from "patchinko/explicit";
import io from "socket.io-client";

import { EventListView } from "event/EventListView.js";
import { EventDetailsView } from "event/EventDetailsView.js";
import { communities, CommunityListView } from "community/CommunityListView.js"

const app = {
    state: {
        events: [],
        persons: [],
        communities: [],
        tagdefs: [],
        competitors: []
    },
    actions: function(update) {
        return {
            /* TODO: Move these to events.actions and merge to app.actions in here */
            loadEvents: function() {
                fetch('/api/event/').then(response => response.json())
                    .then( response => update({ events: response }))
                    .catch(error => {
                        // TODO: error handling
                        console.error("Error: ", error);
                    });
                    // TODO: time sync!
            },
            createEvent: function(event) {
                // TODO: set hourglass
                fetch('/api/event/' + event.id, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(event)
                    })
                    .then(response => response.json())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('createEvent() succeeded'); })
                    .catch(error => {
                        // TODO: error handling, clear hourglass
                        console.error("Error: ", error);
                    });

                // TODO: update based on notification (rather than response)
                // TODO: time sync!
            },
            onEventCreated: function(event) {
                update( { events: app.state.events.concat(event) })
            },
            updateEvent: function(event) {
                fetch('/api/event/' + event.id, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        bod: JSON.stringify(event)
                    })
                    .then(response => response.json())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('updateEvent() succeeded')
                    })
                    .catch(error => {
                        // TODO: error handling, clear hourglass
                        console.error("Error: ", error);
                    });
            },
            removeEvent: function(id) {
                fetch('/api/event/' + id, { method: "DELETE" })
                    .then(response => response.text())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('removeEvent() succeeded'); })
                    .catch(error => {
                        // TODO: Error handler, clear hourglass
                        console.error("Error: ", error);
                    });
                    // TODO: Update based on notification (rather than response)
                    // TODO: time sync!
            },
            onEventRemoved: function(id) {
                update( { events: app.state.events.filter(e => e.id != id) })
            }
        };
    }
};

class App extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Router>
                <div style={{height: "100%"}}>
                    <Route exact path="/" render={() => 
                        <EventListView  states={this.props.states}
                                        actions={this.props.actions}/>} />
                    <Route exact path="/event/" render={() => 
                        <EventListView  states={this.props.states}
                                        actions={this.props.actions}/>} />
                    <Route path="/event/:id" render={(props) =>
                        <EventDetailsView id={props.match.params.id}
                                        states={this.props.states} actions={this.props.actions}/>} />
                    <Route exact path="/community/" render={() =>
                        <CommunityListView states={this.props.states}
                                            actions={this.props.actions} />} />
                </div>
            </Router>
        );
    }
}


const update = flyd.stream();
const states = flyd.scan(P, app.state, update);
const actions = app.actions(update);

render(
    <App states={states} actions={actions} />,
    document.getElementById("app")
);

var socket = io.connect(
    'http://' + document.domain + ':' + location.port + '/api/notifications');
socket.on('connect', () => { console.log('connected!'); });
socket.on('notification', (msg) => {
    if (msg.event) {
        const parts = msg.event.split(/\s+/);

        if (parts[0] === 'CREATED' && parts[1] === 'Event') {
            console.log(msg.data);
            actions.onEventCreated(msg.data);
        }

        if (parts[0] === 'UPDATED' && parts[1] === 'Event') {
            console.log(msg.data);
            actions.onEventUpdated(msg.data);
        }

        if (parts[0] == 'REMOVED' && parts[1] === 'Event') {
            console.log(msg.data);
            actions.onEventRemoved(msg.data);
        }
    }
});