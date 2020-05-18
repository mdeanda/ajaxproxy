const React = require('react');
import PropTypes from 'prop-types';

import RequestList from '../components/requestlist';

class RequestTab extends React.Component {
    constructor(props) {
        super(props);
        this.editRef = React.createRef();
    }

    render() {
        return (
            <div>
                <p>tab starts here</p>

                <RequestList callback={this.itemSelected}/>


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

