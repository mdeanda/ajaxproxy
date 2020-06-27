const React = require('react');
import PropTypes from 'prop-types';


class ProxyRequestEdit extends React.Component {
    static propTypes = {
        serverId: PropTypes.number.isRequired
    };

    state = {
        proxy: null
    };

    constructor(props) {
        super(props);
    };

    render() {
        var label = <h4>Proxy Edit</h4>;
        if (this.state.proxy == null) {
            label = <h4>Add Proxy</h4>;
        }

        let proxy = {
            path: {
                originalValue: null
            }
        };

        if (this.state.proxy != null) {
            proxy = this.state.proxy;
            console.log("edit proxy: ", proxy);
        }

        return (
            <div>
                {label}

                <form onSubmit={this.handleSave}>
                    <div className="row">
                        <div className="col-25">
                            <label htmlFor="proxyPort">Port</label>
                        </div>
                        <div className="col-75">
                            <input type="text"
                                    name="port"
                                    defaultValue={proxy.port}
                                    onChange={this.handleInputChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-25">
                            <label>Path</label>
                        </div>
                        <div className="col-75">
                            <input type="text"
                                    name="path"
                                    defaultValue={proxy.path.originalValue}
                                    onChange={this.handleInputChange} />
                        </div>
                    </div>


                    <div className="row">
                        <div className="col-25 skip-25">
                            <input type="checkbox"
                                    id="enableCache"
                                    name="enableCache"
                                    checked={proxy.enableCache}
                                    onChange={this.handleInputChange}
                                    />
                            <label htmlFor="enableCache">Enable Cache</label>
                        </div>
                    </div>

                    <div className="row submit-row">
                        <div className="col-25 skip-25">
                            <input type="submit"/>
                        </div>
                    </div>
                </form>
            </div>
        )
    }

    componentDidMount() {
        if (!this.props.proxyId) return;

        fetch('/api/config/server/' + this.props.serverId + "/proxy/" + this.props.proxyId)
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            this.setState({proxy:data});
        })
        .catch(console.log)
    }

    handleInputChange = event => {
        const target = event.target;
        const value = target.name === 'enableCache' ? target.checked : target.value;
        const name = target.name;

        var proxy = this.state.proxy;
        if (proxy == null) {
            proxy = {
                path: {},
                host: {}
            };
        }
        switch(name) {
            case 'port':
                proxy.port = value;
                break;
            case 'protocol':
                proxy.protocol = value;
                break;
            case 'hostHeader':
                proxy.hostHeader = value;
                break;
            case 'path':
                proxy.path.originalValue = value;
                break;
            case 'host':
                proxy.host.originalValue = value;
                break;
            case 'enableCache':
                proxy.enableCache = value;
                break;
        }

        this.setState({proxy: proxy});
    }

    handleSave = event => {
        event.preventDefault();

        var uri = '/api/config/server/' + this.props.serverId + "/proxy/" + this.props.proxyId;
        var payload = this.state.proxy;

        //TODO: edit vs add differences
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        };
        fetch(uri, requestOptions)
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


class ProxyEdit extends React.Component {
    static propTypes = {
        serverId: PropTypes.number.isRequired,
//        proxy: PropTypes.object.isRequired,
        proxyType: PropTypes.string.isRequired,
    };

    state = {
    }

    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.proxyType.toUpperCase() == 'PROXY') {
            return (
                <ProxyRequestEdit
                        serverId={this.props.serverId}
                        proxyId={this.props.proxyId} />
            )
        }

        return (
            <div>
                <h2>Editor not available yet</h2>
            </div>
        )
    }
}

export default ProxyEdit

