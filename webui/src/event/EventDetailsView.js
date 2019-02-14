import { View } from "../common/View.js";
import React, { Component } from "react";

class EventDetailsContent extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <p>TODO: Event details for id: {this.props.id}</p>
        );
    }
}

export class EventDetailsView extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View>
                <EventDetailsContent id={this.props.id}
                    states={this.props.states} actions={this.props.actions}/>
            </View>
        );
    }
}
