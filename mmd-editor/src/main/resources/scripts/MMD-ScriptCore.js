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

class Entity {
    constructor() {
        console.log('Main constructor!');
        this.logText("Test", 1);

        let pos = new Vector3(5, 2.5, 1);
        let result = this.logVector(pos);
        console.log(`${result.x}, ${result.y}, ${result.z}`);
        console.log(`${this.logVectorDot(pos)}`);
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
        Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(`${text}`, parameter);
    }
    logVector(parameter) {
         let vector3fClass = Java.type("org.joml.Vector3f");
         return Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(new vector3fClass(parameter.x, parameter.y, parameter.z), new vector3fClass());
    }
    logVectorDot(parameter) {
        let vector3fClass = Java.type("org.joml.Vector3f");
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(new vector3fClass(parameter.x, parameter.y, parameter.z));
    }
}