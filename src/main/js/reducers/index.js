import { ADD_REQUEST } from "../constants/action-types";

const initialState = {
  requests: []
};

function rootReducer(state = initialState, action) {
  if (action.type === ADD_REQUEST) {
    return Object.assign({}, state, {
      requests: state.requests.concat(action.payload)
    });
  }
  return state;
};

export default rootReducer;
