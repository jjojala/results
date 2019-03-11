import './View.css'
import React, { Component } from "react";
import { Link } from "react-router-dom";


export class View extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="view">
                <div className="view-header">
                    <div className="view-header view-header__title">TuPal</div>
                    <div className="view-header view-header__clock">13.55:51</div>
                </div>
                <div className="view-body">
                    <div className="view-content">
                        <div className="view-menu">
                            <div><Link to="/community/">Communities</Link></div>
                            <div><Link to="/event/">Events</Link></div>
                        </div>
                        <div className="view-item">
                            {this.props.children}
                        </div>
                    </div>
                </div>
                <div className="view-footer"/>
            </div>
        );
    }
}
