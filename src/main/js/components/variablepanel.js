const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/serveredit.scss';

import {
  useParams
} from "react-router-dom";

import ProxyEdit from 'components/proxyedit';
import ProxyList from 'components/proxylist';


class VariablePanel extends React.Component {
    static propTypes = {
        //serverId: PropTypes.number.isRequired
    };

    state = {

    };

    constructor(props) {
        super(props);
    }

    render() {
        if (this.state.server == null || this.state.serverId == null) {
            //return 'empty';
        }

        return (
            <div className="variables-panel">
                <h3>Variables</h3>

            </div>
        )
    }

/*
    componentDidUpdate = (prevProps, prevState, snapshot) => {
        let serverId = this.props.match.params.serverId;
        if (prevProps.match.params.serverId != this.props.match.params.serverId) {
            console.log("update to: " + serverId);
            this.setState({server:null, serverId:serverId});
            this.loadData(serverId);
        }
    }
//*/
    componentDidMount() {
        //this.loadData(this.props.match.params.serverId);
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
}

export default VariablePanel

