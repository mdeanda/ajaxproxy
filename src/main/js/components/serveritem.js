const React = require('react');
import PropTypes from 'prop-types';

class ServerItem extends React.Component {
    static propTypes = {
        callback: PropTypes.func.isRequired,
        server: PropTypes.object.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            server: props.server
        };
    }

    render() {
        return (
            <li onClick={this.selected}>server {this.state.server.id}</li>
        )
    }

    selected = () => {
        this.props.callback(this.state.server);
    }

}

export default ServerItem

