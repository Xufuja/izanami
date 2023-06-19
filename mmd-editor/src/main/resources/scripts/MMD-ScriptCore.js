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
        console.log('Main constructor!')
    }
    printMessage() {
        console.log('Hello World from JavaScript!')
    }
    printInt(value) {
        console.log(`JavaScript says: ${value}`)
    }
    printInts(value1, value2) {
        console.log(`JavaScript says: ${value1} and ${value2}`)
    }
    printCustomMessage(message) {
        console.log(`JavaScript says: ${message}`)
    }
    logText(text) {
        Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(`${text}`, 1);
    }
    logVector3(parameter) {
         let vector3fClass = Java.type("org.joml.Vector3f");
         let vec3 = new Vector3(1.1, 1.2, 1.3);
         let vector3f = new vector3fClass(vec3.x, vec3.y, vec3.z);
         Java.type('dev.xfj.engine.scripting.ScriptGlue').nativeLog(vector3f);
         let vec = new Vector3(vector3f.x, vector3f.y, vector3f.z);
         console.log(vec.x);
     }
}