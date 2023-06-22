module.exports = class InternalCalls {
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