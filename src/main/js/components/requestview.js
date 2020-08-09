const React = require('react');
import PropTypes from 'prop-types';

class RequestView extends React.Component {
    static propTypes = {
        itemId: PropTypes.string.isRequired
    };

    state = {
        itemId: this.props.itemId,
        item: null
    };

    constructor(props) {
        super(props);
    }

    render() {
        if (this.state.itemId == null) {
            return '';
        }

        if (this.state.item == null) {
            return <p>{this.state.itemId}</p>
        }

        var item = this.state.item;
        var requestDate = new Date(item.startTime).toString();

        return (
        <div className="request-view">
            <div className="row">
                <span className="value">{item.id}</span>
            </div>
            <div className="row">
                <span className="label">URL</span>
                <span className="value">{item.url}</span>
            </div>
            <div className="row">
                <span className="label">Method</span>
                <span className="value">{item.method}</span>
            </div>
            <div className="row">
                <span className="label">Path</span>
                <span className="value">{item.path}</span>
            </div>
            <div className="row">
                <span className="value">{item.status}</span>
            </div>
            <div className="row">
                <span className="value">{item.reason}</span>
            </div>
            <div className="row">
                <span className="value">{requestDate}</span>
            </div>
            <div className="row">
                <span className="value">{item.duration}</span>
            </div>
            <div className="row">
                <span className="label">Error Message</span>
                <span className="value">{item.errorMessage}</span>
            </div>
            <div className="row">
                <span className="label">Encoding</span>
                <span className="value">{item.contentEncoding}</span>
            </div>

            <div className="row">
                <span className="label">Request Headers</span>
                <span className="value">
                    <pre>{item.headers}</pre>
                </span>
            </div>
            <div className="row">
                <span className="label">Response Headers</span>
                <span className="value">
                    <pre>{item.responseHeaders}</pre>
                </span>
            </div>

            <div className="row">
                <span className="label">Input</span>
                <span className="value">{item.input}</span>
            </div>
            <div className="row">
                <span className="label">Input Text</span>
                <span className="value">{item.inputText}</span>
            </div>
            <div className="row">
                <span className="label">Output</span>
                <span className="value">{item.output}</span>
            </div>
            <div className="row">
                <span className="label">Output Text</span>
                <span className="value">{item.outputText}</span>
            </div>
        </div>
        )
    }

    componentDidMount() {
        var itemId = this.state.itemId;
        if (itemId != null) {
            this.loadData(itemId);
        }
    }

    loadData = itemId => {
        if (itemId != null) {
            fetch('/api/requests/' + itemId)
                .then(res => res.json())
                .then((data) => {
                    console.log("data", data);
                    this.setState({item:data})
                })
                .catch(console.log)
        }
    }
}

export default RequestView

