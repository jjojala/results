import React, { Component } from "react";
import { Link } from "react-router-dom";


export class View extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="_view">
                <div className="_header primary-0">
                    <div className="_title" style={{fontVariant: "small-caps"}}>TuPal</div>
                    <div className="_clock">13.55:51</div>
                    <div><Link to="/community/">Communities</Link></div>
                    <div><Link to="/event/">Events</Link></div>                    
                </div>
                <div  className="_flex_growable primary-1">
                    <div className="_body_wrapper">
                        {this.props.children}
                    </div>
                </div>
                <div className="_footer primary-0"/>
            </div>
        );
    }
}
