import InternalCalls from './internal-calls';

export default class Input {
    static isKeyDown(keyCode) {
        return InternalCalls.inputIsKeyDown(keyCode);
    }
}