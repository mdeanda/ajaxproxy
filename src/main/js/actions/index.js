import { ADD_REQUEST } from "../constants/action-types";

export function addRequest(payload) {
  return { type: ADD_REQUEST, payload };
}
