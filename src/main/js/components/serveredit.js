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

        let urls;

        if (this.state.server.baseUrls.length > 0) {
            urls = <p>Base URL:
                {this.state.server.baseUrls.map((url) => (
                    <span>
                        <a href={url} target='_blank'>{url}</a>
                    </span>
                ))}
            </p>;
        }


        return (
            <div>
                <p>server edit {this.state.server.id}</p>

                {urls}

                <form onSubmit={this.handleSave}>
                    <label>local port</label>
                    <input type="text"
                            name="port"
                            value={this.state.server.port.originalValue}
                            onChange={this.handleInputChange} />

                    <label>resource base</label>
                    <input type="text"
                            name="resourceBase"
                            value={this.state.server.resourceBase.originalValue}
                            onChange={this.handleInputChange} />

                    <label>show directory index</label>
                    <input type="checkbox"
                            name="showIndex"
                            checked={this.state.server.showIndex}
                            onChange={this.handleInputChange}
                            />

                    <input type="submit"/>
                </form>
            </div>
        )
    }

    setServer = item => {
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

export default ServerEdit

