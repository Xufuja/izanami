class Vector3 {
    #float = Java.type('java.lang.Float');
    #x;
    #y;
    #z;

    constructor(x, y, z) {
        this.#x = x;
        this.#y = y;
        this.#z = z;
    }
    get x() {
        return new this.#float(this.#x);
    }
    get y() {
        return new this.#float(this.#y);
    }
    get z() {
        return new this.#float(this.#z);
    }
}

class InternalCalls {
    static vector3f = Java.type("org.joml.Vector3f");
    static logText(text, parameter) {
        Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(`${text}`, parameter);
    }
    static logVector(parameter) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(new InternalCalls.vector3f(parameter.x, parameter.y, parameter.z), new InternalCalls.vector3f());
    }
    static logVectorDot(parameter) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(new InternalCalls.vector3f(parameter.x, parameter.y, parameter.z));
    }
}

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