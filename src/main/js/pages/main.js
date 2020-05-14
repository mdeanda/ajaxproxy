const React = require('react');
const ReactDOM = require('react-dom');

import { Tabs, Tab, Panel } from '@bumaga/tabs'
import Test from '../components/test';
import Servers from '../components/servers';

class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <h1>AjaxProxy</h1>

                <Test />


                <Tabs>
                    <div>
                        <Tab><button>Servers</button></Tab>
                        <Tab><button>Requests</button></Tab>
                        <Tab><button>Variables</button></Tab>
                        <Tab><button>Logger</button></Tab>
                    </div>

                    <Panel><Servers/></Panel>
                    <Panel><p>Panel 2</p></Panel>
                    <Panel><Test /></Panel>
                    <Panel><p>logger</p></Panel>
                </Tabs>
            </div>
        )
    }

    componentDidMount() {

    }

}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

