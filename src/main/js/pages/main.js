const React = require('react');
const ReactDOM = require('react-dom');
import { Provider } from "react-redux";

import styles from '../stylesheets/main.scss';

import { HashRouter as Router, Route, Link, NavLink, Switch } from "react-router-dom";

import store from "store/index";
import Test from 'components/test';
import ServerTab from 'components/servertab';
import ApControl from 'components/apcontrol';
import RequestTab from 'components/requesttab';

import { addRequest } from "../actions/index";

window.store = store;
window.addRequest = addRequest;
//


function loadData() {
    fetch('/api/requests')
        .then(res => res.json())
        .then((data) => {
            //this.setState({requests:data})
            data.forEach(a => {
                console.log("request data", a);
                store.dispatch(addRequest(a));
            });
        })
        .catch(console.log)
}
setTimeout(loadData, 10000);

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
                            <NavLink to='/servers' className='tab'>Servers</NavLink>
                            <NavLink to='/requests' className='tab'>Requests</NavLink>
                            <NavLink to='/variables' className='tab'>Variables</NavLink>
                            <NavLink to='/logger' className='tab'>Logger</NavLink>
                        </nav>
                        <ApControl />
                    </div>
                </div>

                <div className="tabs-container">
                    <Route path="/servers"><ServerTab /></Route>
                    <Route path="/requests"><RequestTab /></Route>
                    <Route path="/variables"><Test /></Route>
                    <Route path="/logger"><p>logger</p></Route>
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

