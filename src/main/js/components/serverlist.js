const React = require('react');
import PropTypes from 'prop-types';

import { HashRouter as Router, Route, Link, NavLink, Switch } from "react-router-dom";

import ServerItem from 'components/serveritem';

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

                    <li key={server.id} className={this.state.selected==server.id?'selected':'not-selected'}>
                        <Link to={'/config/server/' + server.id}>Server {server.id}</Link>
                    </li>
                ))}

                    <li className={this.state.selected=='variables'?'selected':'not-selected'}>
                        <Link to="/config/variables">Variables</Link>
                    </li>
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
            this.setState({servers:data});

            if (this.state.selected == null && data.length > 0) {
                //this.itemSelected(data[0]);
            }
        })
        .catch(console.log)
    }

    itemSelected = item => {
        this.setState({selected: item.id});
        console.log("item selected on serverlist", item);
        this.state.callback(item);
    }

    selectVariables = () => {
        this.setState({selected: 'variables'});
    }
}

export default ServerList

