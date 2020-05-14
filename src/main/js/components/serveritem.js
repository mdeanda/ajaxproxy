const React = require('react');
import PropTypes from 'prop-types';

class ServerItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            server: props.server
        };
    }

    render() {
        return (
            <li>server {this.state.server.id}</li>
        )
    }

}

export default ServerItem

