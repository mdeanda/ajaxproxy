const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/serveredit.scss';

import {
  useParams
} from "react-router-dom";

import ProxyEdit from 'components/proxyedit';
import ProxyList from 'components/proxylist';


class ServerEdit extends React.Component {
    static propTypes = {
        //serverId: PropTypes.number.isRequired
    };

    state = {
        serverId: null,
        server: null,
        selectedProxy: null
    };

    constructor(props) {
        super(props);
    }

    render() {
        if (this.state.server == null || this.state.serverId == null) {
            return '';
        }

        let urls;
        let selectedProxyKey;

        if (this.state.server.baseUrls.length > 0) {
            urls = <p>Base URL:
                {this.state.server.baseUrls.map((url) => (
                    <span key={url}>
                        <a href={url} target='_blank'>{url}</a>
                    </span>
                ))}
            </p>;
        }

        if (this.state.selectedProxy != null) {
            var proxy = this.state.selectedProxy;
            selectedProxyKey = proxy.id + "x"; //TODO: make it change?
        }



        return (
            <div className="server-edit">
                <h3>Server {this.state.serverId}</h3>

                {urls}

                <form onSubmit={this.handleSave}>
                    <div className="row">
                        <div className="col-25">
                            <label htmlFor="port">Local Port</label>
                        </div>
                        <div className="col-75">
                            <input type="text"
                                    id="port"
                                    name="port"
                                    defaultValue={this.state.server.port.originalValue}
                                    onChange={this.handleInputChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-25">
                            <label htmlFor="resourceBase">Resource Base</label>
                        </div>
                        <div className="col-75">
                            <input type="text"
                                    id="resourceBase"
                                    name="resourceBase"
                                    defaultValue={this.state.server.resourceBase.originalValue}
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
                            <label htmlFor="showIndex">show directory index</label>
                        </div>
                    </div>

                    <div className="row submit-row">
                        <div className="col-25 skip-25">
                            <input type="submit"/>
                        </div>
                    </div>
                </form>

                <h3>Proxies</h3>

                <ProxyEdit key={selectedProxyKey}
                        serverId={this.state.server.id}
                        proxyId={this.state.selectedProxy ? this.state.selectedProxy.id : null} proxyType='Request' />

                <ProxyList key={this.state.server.id}
                        serverId={this.state.server.id}
                        callback={this.proxySelected} />
            </div>
        )
    }

    componentDidUpdate = (prevProps, prevState, snapshot) => {
        let serverId = this.props.match.params.serverId;
        if (prevProps.match.params.serverId != this.props.match.params.serverId) {
            console.log("update to: " + serverId);
            this.setState({server:null, serverId:serverId});
            this.loadData(serverId);
        }
    }

    componentDidMount() {
        this.loadData(this.props.match.params.serverId);
    }

    loadData(serverId) {
        console.log("loading server edit: ", serverId);
        if (serverId === null) return;

        fetch('/api/config/server/' + serverId)
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            this.setState({serverId:serverId, server:data, selectedProxy:null});

            if (this.state.selected == null && data.length > 0) {
                //this.itemSelected(data[0]);
            }
        })
        .catch(console.log)
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

    proxySelected = item => {
        this.setState({selectedProxy: item});
    }
}

export default ServerEdit

