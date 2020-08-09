import store from "store/index";
import { requestAdd } from "../actions/index";




class ResourceDataSourceImpl {
    constructor() {
    }

    loadMore() {
        console.log("loading more here!");
    }

    loadData() {
        fetch('/api/requests/first')
            .then(res => res.json())
            .then((data) => {
                //this.setState({requests:data})
                var temp = data;// data.splice(0, 10);
                store.dispatch(requestAdd(temp));
            })
            .catch(console.log)
    }

}

const ResourceDataSource = new ResourceDataSourceImpl();

setTimeout(() => {
    ResourceDataSource.loadData();
}, 100);


export default ResourceDataSource