const React = require('react');
import PropTypes from 'prop-types';

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

        if (this.state.status == RUNNING) {
            return (
                <span onClick={this.stopServer}>stop server</span>
            )
        } else {
            return (
                <span onClick={this.startServer}>start server</span>
            )
        }
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

