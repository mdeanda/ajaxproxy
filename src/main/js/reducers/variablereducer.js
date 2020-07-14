import { VARIABLE_ADD } from "../constants/action-types";

const initialState = {
  variables: []
};

function variableReducer(state = initialState, action) {
  if (action.type === VARIABLE_ADD) {
    return Object.assign({}, state, {
      variables: state.variables.concat(action.payload)
    });
  }
  return state;
};

export default variableReducer;
