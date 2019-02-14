import React, { Component } from "react";
import { Link } from "react-router-dom";
import { randomUUID } from "../util";
import { View } from "../common/View.js";

const communities = {
    state: {
        communities: []
    },
    actions: function(update) {
        return {
            /* TODO: Move these to events.actions and merge to app.actions in here */
            loadCommunities: function() {
                fetch('/api/community/').then(response => response.json())
                    .then( response => update({ events: response }))
                    .catch(error => {
                        // TODO: error handling
                        console.error("Error: ", error);
                    });
                    // TODO: time sync!
            },
            createCommunity: function(community) {
                // TODO: set hourglass
                fetch('/api/community/' + community.id, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(community)
                    })
                    .then(response => response.json())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('Community.create() succeeded'); })
                    .catch(error => {
                        // TODO: error handling, clear hourglass
                        console.error("Error: ", error);
                    });

                // TODO: update based on notification (rather than response)
                // TODO: time sync!
            },
            onCommunityCreated: function(community) {
                update( { communities: app.state.communities.concat(community) })
            },
            updateCommunity: function(community) {
                fetch('/api/community/' + event.id, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        bod: JSON.stringify(community)
                    })
                    .then(response => response.json())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('Community.update() succeeded')
                    })
                    .catch(error => {
                        // TODO: error handling, clear hourglass
                        console.error("Error: ", error);
                    });
            },
            removeCommunity: function(id) {
                fetch('/api/community/' + id, { method: "DELETE" })
                    .then(response => response.text())
                    .then(response => {
                        // TODO: clear hourglass
                        console.log('Community.remove() succeeded'); })
                    .catch(error => {
                        // TODO: Error handler, clear hourglass
                        console.error("Error: ", error);
                    });
                    // TODO: Update based on notification (rather than response)
                    // TODO: time sync!
            },
            onCommunityRemoved: function(id) {
                update( { communities: app.state.communities.filter(e => e.id != id) })
            }
        };
    }
};


class CommunityListContent extends Component {
    constructor(props) {
        super(props);
        this.state = props.states();
    }
    
    componentDidMount() {
        var setState = this.setState.bind(this);
        this.props.states.map(function(state) {
            setState(state);
        });
        this.props.actions.loadEvents();
    }

    render() {
        var state = this.state;
        var actions = this.props.actions;
        const id = randomUUID();
        
        const items = state.events.map(e => {
            return (
                <div key={e.id} style={{display: "flex", flexDirection: "row"}}>
                    <span style={{width: "200px"}}>
                        {new Date(e.date).toLocaleString('fi-FI')}
                    </span>
                    <span style={{flexGrow: "1"}}>
                        {e.name}
                    </span>
                    <span style={{right: "0", width: "70px"}}>
                        <button onClick={() => actions.removeEvent(e.id)}>
                            Poista
                        </button>
                    </span>
                    <span style={{width: "70px"}}>
                        <Link to={"/event/" + e.id}>Hee</Link>
                    </span>
                </div>
            );
        });
        
        return(
            <div style={{display: "flex", flexDirection: "column"}}>
                <div style={{display: "flex", flexDirection: "row", backgroundColor: "lightgray", marginBottom: "16px", fontWeight: "bold"}}>
                    <span style={{width: "200px"}}>
                        P&auml;iv&auml; ja kellonaika
                    </span>
                    <span style={{flexGrow: "1"}}>
                        Tapahtuman nimi
                    </span>
                    <span style={{right: "0", width: "70px"}}>
                        <button onClick={() => actions.createEvent({ 
                            id: id, date: "2019-02-05T10:42:14.000+03:00",
                            name: "Name (id=" + id + ")"})}>
                            Lis&auml;&auml;
                        </button>
                    </span>
                </div>
                {items}
            </div>
        );
    }
}

export class CommunityListView extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View>
                <CommunityListContent actions={this.props.actions}
                        states={this.props.states}/>
            </View>
        );
    }
}
