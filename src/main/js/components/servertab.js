const React = require('react');
import PropTypes from 'prop-types';

import styles from '../stylesheets/servertab.scss';


import ServerList from '../components/serverlist';
import ServerEdit from '../components/serveredit';

class ServerTab extends React.Component {
    constructor(props) {
        super(props);
        this.editRef = React.createRef();
    }

    render() {
        return (
            <div class="server-tab">
                <div class="list">
                    <ServerList callback={this.itemSelected}/>
                </div>
                <div class="content">
                    <ServerEdit ref={this.editRef}/>
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

export default ServerTab

