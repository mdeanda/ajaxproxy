const React = require('react');
import PropTypes from 'prop-types';

//import ServerItem from '../components/serveritem';

class ProxyList extends React.Component {
    static propTypes = {
        serverId: PropTypes.number.isRequired
    };

    state = {
        serverId: this.props.serverId
    }

    constructor(props) {
        super(props);
    }

    render() {
        if (this.state == null || this.state.items == null) {
            return '';
        }

        return (
            <div className="proxy-list">
                <ul>
                {this.state.items.map((proxy) => (
                    <li key={proxy.id}>
                        <span className="id">{proxy.id}</span>
                        --
                        <span className="protocol">{proxy.protocol}</span>
                        ://
                        <span className="host">{proxy.host.originalValue}</span>
                        :
                        <span className="port">{proxy.port}</span>
                        <span className="path">{proxy.path.originalValue}</span>
                        
                        <span className="host-header">{proxy.hostHeader}</span>

                        <span className="enable-cache">{proxy.enableCache}</span>
                        <span className="cache-duration">{proxy.cacheDuration}</span>
                    </li>
                ))}
                </ul>
            </div>
        )
    }

    componentDidMount() {
        this.loadData();
    }

    loadData() {
        fetch('/api/config/server/' + this.state.serverId + '/proxy')
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            this.setState({items:data})
        })
        .catch(console.log)
    }

    itemSelected = item => {
        console.log("item selected on ProxyList", item);
        this.state.callback(item);
    }
}

export default ProxyList

