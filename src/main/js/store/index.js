import { createStore } from "redux";
import rootReducer from "reducers/index";
import variableReducer from "reducers/variablereducer"

const store = createStore(rootReducer);
const variableStore = createStore(variableReducer);

export default store;
export {variableStore};
