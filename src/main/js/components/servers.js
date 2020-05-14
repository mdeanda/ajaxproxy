const React = require('react');
import PropTypes from 'prop-types';

class Servers extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.state == null) {
            return '';
        }

        return (
            <p>this is a sss</p>
        )
    }

    componentDidMount() {
        this.loadData();
    }

    loadData() {
        fetch('/api/config/server')
        .then(res => res.json())
        .then((data) => {
            console.log("data", data);
            //this.setState(data)
        })
        .catch(console.log)
    }
}

export default Servers

