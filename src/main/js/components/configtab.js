const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/configtab.scss';

import { HashRouter as Router, Route, Link, NavLink, Switch } from "react-router-dom";

import ServerList from 'components/serverlist';
import ServerEdit from 'components/serveredit';
import VariablePanel from 'components/variablepanel';

class ConfigTab extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="config-tab">
                <div className="list">
                    <ServerList callback={this.itemSelected}/>
                </div>
                <div className="content">
                    <Switch>
                        <Route exact path="/config/server/:serverId" component={ServerEdit} />
                        <Route path="/config/variables"><VariablePanel /></Route>
                        <Route>
                            <p>Configure AjaxProxy here!</p>
                        </Route>
                    </Switch>
                </div>
            </div>
        )

    }

    itemSelected = item => {

    }

}

export default ConfigTab

