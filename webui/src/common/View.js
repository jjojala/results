import './View.css';
import CommunityImg from 'images/svg/011-community.svg';
import NamesImg from 'images/svg/012-group.svg';
import EventsImg from 'images/svg/013-calendar.svg'
import React, { Component } from "react";
import { Link } from "react-router-dom";
import { Z_BLOCK } from 'zlib';


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
                    <div className="view-body-container">
                        <div className="view-body-menu">
                            <div className="view-body-menu__item">
                                <Link to="/event/">
                                    <EventsImg
                                        height={48} width={48}/>
                                </Link>
                            </div>
                            <div className="view-body-menu__item">
                                <Link to="/community/">
                                    <CommunityImg
                                        height={48} width={48}/>
                                </Link>
                            </div>
                            <div className="view-body-menu__item">
                                <Link to="/name/">
                                    <NamesImg
                                        height={48} width={48} />
                                </Link>
                            </div>
                        </div>
                        <div className="view-body-items">
                            {this.props.children}
                        </div>
                    </div>
                </div>
                <div className="view-footer"/>
            </div>
        );
    }
}
