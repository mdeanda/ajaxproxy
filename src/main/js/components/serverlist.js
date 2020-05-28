const React = require('react');
import PropTypes from 'prop-types';

import ServerItem from '../components/serveritem';

class ServerList extends React.Component {
    static propTypes = {
        callback: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            servers: null,
            selected: null,
            callback: props.callback
        };

        console.log("props", props);
    }

    render() {
        console.log("render serverlist");
        if (this.state == null || this.state.servers == null) {
            return '';
        }

        return (
            <div>
                <p>this is the list</p>

                <ul>
                {this.state.servers.map((server) => (
                    <ServerItem server={server}
                            key={server.id + '-' + this.state.selected}
                            callback={this.itemSelected}
                            selected={this.state.selected == server.id}/>
                ))}
                </ul>
            </div>
        )
    }

    componentDidMount() {
        this.loadData();
    }

    loadData() {
        fetch('/api/config/server')
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            this.setState({servers:data})
        })
        .catch(console.log)
    }

    itemSelected = item => {
        this.setState({selected: item.id});
        console.log("item selected on serverlist", item);
        this.state.callback(item);
    }
}

export default ServerList

