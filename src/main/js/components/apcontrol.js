const React = require('react');
import PropTypes from 'prop-types';

import { faPlay, faStop, faRedo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";


const RUNNING = 'RUNNING';
const STOPPED = 'STOPPED';

class ApControl extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            status: null
        }
    }

    render() {
        if (this.state.status == null) return '';

        var playCss = "play";
        var stopCss = "stop";
        var refreshCss = "refresh";

        if (this.state.status == RUNNING) {
            playCss += " active";
        } else {
            stopCss += " active";
        }

        return (
            <div className="ap-control">
                <div className="buttons">
                    <span onClick={this.startServer} className={playCss} title="Start Server"><FontAwesomeIcon icon={faPlay} /></span>
                    <span onClick={this.stopServer} className={stopCss} title="Stop Server"><FontAwesomeIcon icon={faStop} /></span>
                    <span onClick={this.restartServer} className={refreshCss} title="Restart Server"><FontAwesomeIcon icon={faRedo} /></span>
                </div>
                <div className="status">{this.state.status}</div>
            </div>
        )

    }

    componentDidMount() {
        fetch('/api/server/status')
                .then(res => res.json())
                .then((data) => {
                    console.log("status", data);
                    this.setState(data)
                })
                .catch(console.log)
    }

    startServer = () => {
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
            //body: JSON.stringify({ content: this.state.value })
        };

        fetch('/api/server/start', requestOptions)
                .then((data) => {
                    this.setState({status: RUNNING});
                })
                .catch(console.log)
    }

    stopServer = () => {
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
            //body: JSON.stringify({ content: this.state.value })
        };

        fetch('/api/server/stop', requestOptions)
                .then((data) => {
                    this.setState({status: STOPPED});
                })
                .catch(console.log)
    }

}

export default ApControl

