const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/requesttab.scss';

import RequestList from '../components/requestlist';

class RequestTab extends React.Component {
    constructor(props) {
        super(props);
        this.editRef = React.createRef();
    }

    render() {
        return (
            <div className="request-tab">
                <div className="list">
                    <RequestList callback={this.itemSelected}/>
                </div>
                <div className="content">
                    <p>content goes here</p>
                </div>
            </div>
        )
    }

    itemSelected = item => {
        console.log("item selected", item);

        const node = this.editRef.current;
        node.setServer(JSON.parse(JSON.stringify(item)));
    }

}

export default RequestTab

