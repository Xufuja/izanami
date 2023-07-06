import InternalCalls from './InternalCalls';

export default class Input {
    static isKeyDown(keyCode) {
        return InternalCalls.inputIsKeyDown(keyCode);
    }
}