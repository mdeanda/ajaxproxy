const React = require('react');
import PropTypes from 'prop-types';

//import ServerItem from '../components/serveritem';

class ProxyList extends React.Component {
    static propTypes = {
            serverId: PropTypes.number.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            serverId: props.serverId
        };

    }

    render() {
        if (this.state == null || this.state.items == null) {
            return '';
        }

        return (
            <div class="proxy-list">
                proxy list
                <ul>
                {this.state.items.map((proxy) => (
                    <li>
                        <span class="id">{proxy.id}</span>
                        <span class="protocol">{proxy.protocol}</span>
                        <span class="host">{proxy.host.originalValue}</span>
                        <span class="port">{proxy.port}</span>
                        <span class="path">{proxy.path.originalValue}</span>
                        <span class="host-header">{proxy.hostHeader}</span>

                        <span class="enable-cache">{proxy.enableCache}</span>
                        <span class="cache-duration">{proxy.cacheDuration}</span>
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

