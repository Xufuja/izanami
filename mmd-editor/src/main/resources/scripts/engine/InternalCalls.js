const Vector3 = require("./Vector3");

module.exports = class InternalCalls {
    static vector2f = Java.type("org.joml.Vector2f");
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
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').entityHasComponent(entityId, InternalCalls.getComponentType(componentType));
    }
    static getComponentType(componentType) {
        return Java.type(`dev.xfj.engine.scene.components.${componentType.name}`);
    }
    static transformComponentGetTranslation(entityId) {
        let vec3 = Java.type('dev.xfj.engine.scripting.ScriptGlue').transformComponentGetTranslation(entityId);
        return new Vector3(vec3.x, vec3.y, vec3.z);
    }
    static transformComponentSetTranslation(entityId, translation) {
        let vec3 = Java.type('dev.xfj.engine.scripting.ScriptGlue').transformComponentSetTranslation(entityId, new InternalCalls.vector3f(translation.x, translation.y, translation.z));
        return new Vector3(vec3.x, vec3.y, vec3.z);
    }
    static rigidbody2DComponentApplyLinearImpulse(entityId, impulse, point, wake) {
        Java.type('dev.xfj.engine.scripting.ScriptGlue').rigidbody2DComponentApplyLinearImpulse(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), new InternalCalls.vector2f(point.x, point.y), wake);
    }
    static rigidbody2DComponentApplyLinearImpulseToCenter(entityId, impulse, wake) {
        Java.type('dev.xfj.engine.scripting.ScriptGlue').rigidbody2DComponentApplyLinearImpulseToCenter(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), wake);
    }
    static inputIsKeyDown(keyCode) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').inputIsKeyDown(keyCode);
    }
}