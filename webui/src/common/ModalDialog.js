import './ModalDialog.css';
import React, { Component } from 'react';

export class ModalDialog extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        const backdropStyle = {
            position: 'fixed',
            top: 0,
            bottom: 0,
            left: 0,
            right: 0,
            backgroundColor: 'rgba(0,0,0,0.3)',
            padding: 50
        };
      
        // The modal "window"
        const modalStyle = {
            backgroundColor: '#fff',
            borderRadius: 5,
            maxWidth: 500,
            margin: '0 auto',
            padding: 30
        };
      
        if (!this.props.show) {
            return null;
        }

        return (
            <div style={backdropStyle}>
                <div style={modalStyle}>
                    {this.props.children}
                </div>
            </div>
        );
    }
}