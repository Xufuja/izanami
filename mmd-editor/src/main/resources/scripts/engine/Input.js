module.exports = class Input {
    
    static isKeyDown(keyCode) {
        return InternalCalls.inputIsKeyDown(keyCode);
    }
}