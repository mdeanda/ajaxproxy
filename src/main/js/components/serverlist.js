const React = require('react');
import PropTypes from 'prop-types';

import ServerItem from '../components/serveritem';

class ServerList extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.state == null) {
            return '';
        }

        return (
            <div>
                <p>this is a sss</p>

                <ul>
                {this.state.servers.map((server) => (
                    <ServerItem server={server} key={server.id}/>
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
}

export default ServerList

