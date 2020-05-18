const React = require('react');
import PropTypes from 'prop-types';

//import ServerItem from '../components/serveritem';

class RequestList extends React.Component {
    static propTypes = {
        callback: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            requests: null,
            callback: props.callback
        };

        console.log("props", props);
    }

    render() {
        if (this.state == null || this.state.requests == null) {
            return '';
        }

        return (
            <div class="request-list">
                <ul>
                {this.state.requests.map((request) => (
                    <li>
                        <span class="path">{request.path}</span>
                        <span class="method">{request.method}</span>
                        <span class="status">{request.status}</span>
                        <span class="duration">{request.duration}</span>
                    </li>
                ))}
                </ul>
            </div>
        )
    }

    componentDidMount() {
        this.loadData();
    }

    loadData() {
        fetch('/api/requests')
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            this.setState({requests:data})
        })
        .catch(console.log)
    }

    itemSelected = item => {
        console.log("item selected on serverlist", item);
        this.state.callback(item);
    }
}

export default RequestList

