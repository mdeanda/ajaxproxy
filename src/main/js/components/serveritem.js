const React = require('react');
import PropTypes from 'prop-types';

class ServerItem extends React.Component {
    static propTypes = {
        callback: PropTypes.func.isRequired,
        server: PropTypes.object.isRequired,
        selected: PropTypes.bool.isRequired
    };

    state = {
        server: this.props.server,
        selected: this.props.selected
    };

    constructor(props) {
        super(props);
        console.log("server/selected: " + props.selected, props.server, );
        /*
        this.state = {
            server: props.server,
            selected: props.selected
        };
        //*/
    }

    render() {
        return (
            <li onClick={this.selected}
                    className={this.state.selected==true?'selected':'not-selected'}
                    >server {this.state.server.id}</li>
        )
    }

    selected = () => {
        this.props.callback(this.state.server);
    }

}

export default ServerItem

