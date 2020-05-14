const React = require('react');
import PropTypes from 'prop-types';

import ServerList from '../components/serverlist';
import ServerEdit from '../components/serveredit';

class ServerTab extends React.Component {
    constructor(props) {
        super(props);
        this.editRef = React.createRef();
    }

    render() {
        return (
            <div>
                <p>tab starts here</p>

                <ServerList callback={this.itemSelected}/>

                <ServerEdit ref={this.editRef}/>
            </div>
        )
    }

    itemSelected = item => {
        console.log("item selected", item);

        const node = this.editRef.current;
        node.setServer(item);
    }

}

export default ServerTab

