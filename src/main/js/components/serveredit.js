const React = require('react');
import PropTypes from 'prop-types';

class ServerEdit extends React.Component {
    static propTypes = {
    };

    constructor(props) {
        super(props);
        this.state = {
            server: props.server
        };
    }

    render() {
        if (this.state.server == null) {
            return '';
        }

        return (
            <p>server edit {this.state.server.id}</p>
        )
    }

    setServer = item => {
        this.setState({server:item})
    }

}

export default ServerEdit

