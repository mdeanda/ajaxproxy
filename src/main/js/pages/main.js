const React = require('react');
const ReactDOM = require('react-dom');

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
            <div>
                <h1>AjaxProxy</h1>

                <Tabs>
                    <div>
                        <Tab><button>Servers</button></Tab>
                        <Tab><button>Requests</button></Tab>
                        <Tab><button>Variables</button></Tab>
                        <Tab><button>Logger</button></Tab>
                        <ApControl />
                    </div>

                    <Panel><ServerTab /></Panel>
                    <Panel><RequestTab /></Panel>
                    <Panel><Test /></Panel>
                    <Panel><p>logger</p></Panel>
                </Tabs>
            </div>
        )
    }


}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

