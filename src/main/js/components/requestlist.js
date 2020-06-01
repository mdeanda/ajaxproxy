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
            callback: props.callback,
            selectedItem: null
        };

        console.log("props", props);
    }

    render() {
        if (this.state == null || this.state.requests == null) {
            return '';
        }

        return (
            <div className="request-list">
                <ul>
                {this.state.requests.map((request) => (
                    <li key={request.id} className={this.getDurationClass(request.duration) + ' ' + this.getActiveClass(request) }
                            onClick={() => this.selected(request)}>
                        <p className="row-1">
                            <span className="path">{request.path}</span>
                        </p>
                        <p className="row-2">
                            <span className="method">{request.method}</span>
                            <span className="status">{request.status}</span>
                            <span className="duration">{request.duration}ms</span>
                        </p>
                    </li>
                ))}
                </ul>
            </div>
        )
    }

    getActiveClass = request => {
        return this.state.selectedItem != null && request.id == this.state.selectedItem.id
            ? 'active': '';
    }

    getDurationClass = dur => {
        if (dur > 10000)
            return 'dur-5';
        else if (dur > 6000)
            return 'dur-4';
        else if (dur > 4000)
            return 'dur-4';
        else if (dur > 2000)
            return 'dur-3';
        else if (dur > 1000)
            return 'dur-2';
        else if (dur > 500)
            return 'dur-1';
        else
            return 'dur-0';
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

    selected = item => {
        this.setState({selectedItem: item});
        this.state.callback(item);
    }

    shouldComponentUpdate = (nextProps, nextState) => {

        if (nextState.selectedItem == this.state.selectedItem && this.state.selectedItem != null) {
            console.log("dont update");
            return false;
        }


        console.log("request list should update", nextState);

        return true;
    }
}

export default RequestList

