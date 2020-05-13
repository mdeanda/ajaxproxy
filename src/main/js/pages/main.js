const React = require('react');
const ReactDOM = require('react-dom');

class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <h1>AjaxProxy</h1>

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

