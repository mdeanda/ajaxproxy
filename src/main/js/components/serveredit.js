const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/serveredit.scss';


import ProxyEdit from '../components/proxyedit';
import ProxyList from '../components/proxylist';


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
                    <span key={url}>
                        <a href={url} target='_blank'>{url}</a>
                    </span>
                ))}
            </p>;
        }


        return (
            <div className="server-edit">
                <h3>Server {this.state.server.id}</h3>

                {urls}

                <form onSubmit={this.handleSave}>
                    <div className="row">
                        <div class="col-25">
                            <label for="port">Local Port</label>
                        </div>
                        <div class="col-75">
                            <input type="text"
                                    id="port"
                                    name="port"
                                    value={this.state.server.port.originalValue}
                                    onChange={this.handleInputChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div class="col-25">
                            <label for="resourceBase">Resource Base</label>
                        </div>
                        <div class="col-75">
                            <input type="text"
                                    id="resourceBase"
                                    name="resourceBase"
                                    value={this.state.server.resourceBase.originalValue}
                                    onChange={this.handleInputChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-25 skip-25">
                            <input type="checkbox"
                                    id="showIndex"
                                    name="showIndex"
                                    checked={this.state.server.showIndex}
                                    onChange={this.handleInputChange}
                                    />
                            <label for="showIndex">show directory index</label>
                        </div>
                    </div>

                    <div className="row submit-row">
                        <div className="col-25 skip-25">
                            <input type="submit"/>
                        </div>
                    </div>
                </form>

                <h3>Proxy List</h3>

                <ProxyEdit serverId={this.state.server.id} proxyType='Proxy' />

                <ProxyList serverId={this.state.server.id} key={this.state.server.id} />
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

export default ServerEdit

