import React, { Component } from "react";
import { render } from "react-dom";
import { BrowserRouter as Router, Route } from "react-router-dom";
import flyd from "flyd";
import { P } from "patchinko/explicit";

import { EventListView } from "./event/EventListView.js";
import { EventDetailsView } from "./event/EventDetailsView.js";

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
            addEvent: function(event) {
                fetch('/api/event/' + event.id, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(event)
                    })
                    .then(response => response.json())
                    .then(response =>
                        update( { events: app.state.events.concat(event) } ))
                    .catch(error => {
                        // TODO: error handling
                        console.error("Error: ", error);
                    });

                // TODO: update based on notification (rather than response)
                // TODO: time sync!
            },
            removeEvent: function(id) {
                fetch('/api/event/' + id, { method: "DELETE" })
                    .then(response => response.text())
                    .then(response => 
                        update({ events: app.state.events.filter(e => e.id !== id) }))
                    .catch(error => {
                        // TODO: Error handler
                        console.error("Error: ", error);
                    });
                    // TODO: Update based on notification (rather than response)
                    // TODO: time sync!
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