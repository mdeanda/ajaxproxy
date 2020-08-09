const React = require('react');
const ReactDOM = require('react-dom');
import { Provider } from "react-redux";

import styles from '../stylesheets/main.scss';

import { HashRouter as Router, Route, Link, NavLink, Switch } from "react-router-dom";

import store from "store/index";
import ConfigTab from 'components/configtab';
import ApControl from 'components/apcontrol';
import RequestTab from 'components/requesttab';

/*
import { requestAdd } from "../actions/index";
window.store = store;
window.requestAdd = requestAdd;
//*/



class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Router>
                <div className="header">
                    <h1>AjaxProxy</h1>

                    <div className="bottom-bar">
                        <nav>
                            <NavLink to='/config' className='tab'>Configuration</NavLink>
                            <NavLink to='/requests' className='tab'>Requests</NavLink>
                            <NavLink to='/logger' className='tab'>Logger</NavLink>
                        </nav>
                        <ApControl />
                    </div>
                </div>

                <div className="tabs-container">
                    <Switch>
                        <Route path="/config"><ConfigTab /></Route>
                        <Route path="/requests"><RequestTab /></Route>
                        <Route path="/logger"><p>logger</p></Route>
                        <Route>
                            <p>Welcome to this here place</p>
                        </Route>
                    </Switch>
                </div>
            </Router>
        )
    }


}

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    document.getElementById('react')
)

