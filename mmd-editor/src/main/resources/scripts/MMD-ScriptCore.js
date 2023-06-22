const Vector3 = require('./Vector3');
const InternalCalls = require('./InternalCalls');

class Entity {
    constructor() {
        console.log('Main constructor!');
        this.logText("Test", 1);

        let pos = new Vector3(5, 2.5, 1);
        let result = this.logVector(pos);
        console.log(`${result.x}, ${result.y}, ${result.z}`);
        console.log(`${InternalCalls.logVectorDot(pos)}`);
    }
    printMessage() {
        console.log('Hello World from JavaScript!');
    }
    printInt(value) {
        console.log(`JavaScript says: ${value}`);
    }
    printInts(value1, value2) {
        console.log(`JavaScript says: ${value1} and ${value2}`);
    }
    printCustomMessage(message) {
        console.log(`JavaScript says: ${message}`);
    }
    logText(text, parameter) {
        InternalCalls.logText(text, parameter);
    }
    logVector(parameter) {
         return InternalCalls.logVector(parameter);
    }
}