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
    static entityHasComponent(entityId, componentType) {
        //TODO: ScriptGlue
        return true;
    }
    static transformComponentGetTranslation(entityId) {
        //TODO: ScriptGlue
        return true;
    }
    static transformComponentSetTranslation(entityId, translation) {
        //TODO: ScriptGlue
        return true;
    }
    static rigidbody2DComponentApplyLinearImpulse(entityId, impulse, point, wake) {
        //TODO: ScriptGlue
        return true;
    }
    static rigidbody2DComponentApplyLinearImpulseToCenter(entityId, impulse, wake) {
        //TODO: ScriptGlue
        return true;
    }
    static inputIsKeyDown(keyCode) {
        //TODO: ScriptGlue
        return true;
    }
}