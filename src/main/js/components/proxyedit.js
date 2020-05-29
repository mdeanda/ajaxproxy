const React = require('react');
import PropTypes from 'prop-types';


class ProxyRequestEdit extends React.Component {
    static propTypes = {
        serverId: PropTypes.number.isRequired
    };

    state = {
        serverId: this.props.serverId
    };

    constructor(props) {
        super(props);
    };

    render() {
        return (
            <div>
                proxy request editor goes here

                <form onSubmit={this.handleSave}>
                    <label>local port</label>
                    <input type="text"
                            name="port"
                            value={this.state.serverId}
                            onChange={this.handleInputChange} />

                    <label>resource base</label>
                    <input type="text"
                            name="resourceBase"
                            value={this.state.serverId}
                            onChange={this.handleInputChange} />

                    <label>show directory index</label>
                    <input type="checkbox"
                            name="showIndex"
                            checked={this.state.serverId}
                            onChange={this.handleInputChange}
                            />

                    <input type="submit"/>
                </form>
            </div>
        )
    }
}

class ProxyEdit extends React.Component {
    static propTypes = {
        serverId: PropTypes.number.isRequired,
        proxyType: PropTypes.string.isRequired,
    };

    state = {
        serverId: this.props.serverId,
        proxyType: this.props.proxyType
    }

    constructor(props) {
        super(props);
    }

    render() {
        if (this.state.proxyType.toUpperCase() == 'PROXY') {
            return (
                <ProxyRequestEdit serverId={this.state.serverId} />
            )
        }

        return (
            <div>
                <h2>Editor not available yet</h2>
            </div>
        )
    }

    setServer = item => {
        //TODO: maybe just use id instead and reload
        this.setState({server:item})
    }

    handleInputChange = event => {
        const target = event.target;
        const value = target.name === 'showIndex' ? target.checked : target.value;
        const name = target.name;

        var server = this.state.server;
        switch(name) {
            case 'port':
                server.port.originalValue = value;
                break;
            case 'resourceBase':
                server.resourceBase.originalValue = value;
                break;
            case 'showIndex':
                server.showIndex = value;
                break;
        }

        this.setState({server: server});

        console.log("state", this.state);
    }

    handleSave = event => {
        event.preventDefault();

        //TODO: edit vs add differences
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state.server)
        };
        fetch('/api/config/server/' + this.state.server.id, requestOptions)
                .then(res => {
                    if (res.ok)
                        return res;
                    else throw Error("Status error: " + res.status + ": " + res.statusText);
                })
                .then(res => res.json())
                .then((data) => {
                    this.setState({server: data})
                    //TODO: notify listener that server changed
                })
                .catch(console.log);
    }
}

export default ProxyEdit

