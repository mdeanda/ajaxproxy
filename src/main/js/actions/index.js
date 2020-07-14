import { REQUEST_ADD } from "../constants/action-types";
import { VARIABLE_ADD } from "../constants/action-types";

export function requestAdd(payload) {
  return { type: REQUEST_ADD, payload };
}

export function variableAdd(payload) {
    return { type: VARIABLE_ADD, payload };
}

