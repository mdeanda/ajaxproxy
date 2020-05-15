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
        <div>
            <p>server edit {this.state.server.id}</p>

            <form>
                <label>local port</label>
                <input type="text" value={this.state.server.port.originalValue}/>

                <label>resource base</label>
                <input type="text" value={this.state.server.resourceBase.originalValue} />

                <label>show directory index</label>
                <input type="checkbox" checked={this.state.server.showIndex}/>


            </form>
        </div>
        )
    }

    setServer = item => {
        this.setState({server:item})
    }

}

export default ServerEdit

