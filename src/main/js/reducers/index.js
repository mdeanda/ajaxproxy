import { REQUEST_ADD } from "../constants/action-types";

const initialState = {
  requests: [],
  variables: []
};

function rootReducer(state = initialState, action) {
  if (action.type === REQUEST_ADD) {
    return Object.assign({}, state, {
      requests: state.requests.concat(action.payload)
    });
  }
  return state;
};

export default rootReducer;
