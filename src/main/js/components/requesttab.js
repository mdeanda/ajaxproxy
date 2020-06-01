const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/requesttab.scss';

import RequestList from '../components/requestlist';

class RequestTab extends React.Component {
    state = {
        selectedItem: null
    }

    constructor(props) {
        super(props);
        this.editRef = React.createRef();
    }

    render() {
        var selectedId = "x";
        if (this.state.selectedItem != null) {
            selectedId = this.state.selectedItem.id;
        }

        console.log("render request tab: " + selectedId);

        return (
            <div className="request-tab">
                <div className="list">
                    <RequestList callback={this.itemSelected}/>
                </div>
                <div className="content">
                    <p key={selectedId}>content goes here: {selectedId}</p>
                </div>
            </div>
        )
    }

    itemSelected = item => {
        console.log("item selected", item);
        this.setState({selectedItem: item});
    }

}

export default RequestTab

