const React = require('react');
const ReactDOM = require('react-dom');

import styles from '../stylesheets/main.scss';

import { Tabs, Tab, Panel } from '@bumaga/tabs'
import Test from '../components/test';
import ServerTab from '../components/servertab';
import ApControl from '../components/apcontrol';
import RequestTab from '../components/requesttab';

class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Tabs>
                <div className="header">
                    <h1>AjaxProxy</h1>

                    <Tab><button>Servers</button></Tab>
                    <Tab><button>Requests</button></Tab>
                    <Tab><button>Variables</button></Tab>
                    <Tab><button>Logger</button></Tab>
                    <ApControl />
                </div>

                <div className="tabs-container">
                    <Panel><ServerTab /></Panel>
                    <Panel><RequestTab /></Panel>
                    <Panel><Test /></Panel>
                    <Panel><p>logger</p></Panel>
                </div>
            </Tabs>
        )
    }


}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

