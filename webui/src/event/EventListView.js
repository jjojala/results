import './EventListView.css';
import NewImg from 'images/svg/003-more.svg';
import DelImg from 'images/svg/006-rounded-delete-button-with-minus.svg';
import NextImg from 'images/svg/016-next-page.svg';
import OkImg from 'images/svg/004-check-mark-button.svg';
import CancelImg from 'images/svg/005-cancel.svg';
import React, { Component } from "react";
import { Link } from "react-router-dom";
import { randomUUID } from "../util";
import { View } from "../common/View.js";
import { ModalDialog } from '../common/ModalDialog.js';
import { throws } from 'assert';

class NewEventDialog extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <ModalDialog show={this.props.show}>
                <h1>Uusi tapahtuma</h1>
                <button onClick={() => this.props.onClose()}>
                    <CancelImg height={32} width={32}/>
                </button>
                <button onClick={() => this.props.onCreate()}>
                    <OkImg height={32} width={32}/>
                </button>
            </ModalDialog>
        )
    }
}

class EventListContent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            states: props.states(),
            isNewEventDialogOpen: false
        }
    }
    
    componentDidMount() {
        var setState = this.setState.bind(this);
        this.openNewEventDialog.bind(this);
        this.closeNewEventDialog.bind(this);
        this.createNewEvent.bind(this);

        this.props.states.map(function(state) {
            setState({ states: state });
        });
        this.props.actions.loadEvents();
    }

    openNewEventDialog() {
        this.setState({ isNewEventDialogOpen: true });
    }

    closeNewEventDialog() {
        console.log('closeNewEventDialog()')
        // TODO: Reset dialog content...
        this.setState({ isNewEventDialogOpen: false });
    }

    createNewEvent(e) {
        console.log('createNewEvent()')
        const eid = randomUUID();
        this.props.actions.createEvent({
            id: eid, date: "2019-02-05T10:42:14.000+03:00",
            name: "Name (id: " + eid + ")"});
        
        this.setState({ isNewEventDialogOpen: false });
    }

    render() {
        var state = this.state;
        var actions = this.props.actions;
        const id = randomUUID();
        
        const items = state.states.events.map(e => {
            return (
                <div key={e.id} style={{display: "flex", flexDirection: "row"}}>
                    <span style={{width: "200px"}}>
                        {new Date(e.date).toLocaleString('fi-FI')}
                    </span>
                    <span style={{flexGrow: "1"}}>
                        {e.name}
                    </span>
                    <span>
                        <button onClick={() => actions.removeEvent(e.id)}>
                            <DelImg height={32} width={32}/>
                        </button>
                    </span>
                    <span style={{width: "70px"}}>
                        <Link to={"/event/" + e.id}>
                            <button>
                                <NextImg height={32} width={32}/>
                            </button>
                        </Link>
                    </span>
                </div>
            );
        });

        return (
            <div style={{display: "flex", flexDirection: "column", height: "100%"}}>
                <NewEventDialog show={this.state.isNewEventDialogOpen}
                        onClose={() => this.closeNewEventDialog()}
                        onCreate={(e) => this.createNewEvent(e)}>
                </NewEventDialog>

                <div style={{display: "flex", flexDirection: "row", backgroundColor: "lightgray", marginBottom: "16px", fontWeight: "bold"}}>
                    <span style={{width: "200px"}}>
                        Päivä ja aika
                    </span>
                    <span style={{flexGrow: "1"}}>
                        Tapahtuman nimi
                    </span>
                    <span>
                        <button onClick={() => this.openNewEventDialog()}>
                            <NewImg height={32} width={32}/>
                        </button>
                    </span>
                </div>
                <div style={{flexGrow: "1", overflowY: "scroll", height: "0px"}}>
                    {items}
                </div>
            </div>
        );
    }
}

export class EventListView extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View>
                <EventListContent actions={this.props.actions} states={this.props.states}/>
            </View>
        );
    }
}
