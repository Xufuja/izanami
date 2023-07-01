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
        let type;
        switch (componentType) {
            case TransformComponent:
                type = Java.type("dev.xfj.engine.scene.components.TransformComponent");
                break;
            case Rigidbody2DComponent:
                type = Java.type("dev.xfj.engine.scene.components.Rigidbody2DComponent");
                break;
        }
        return type;
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
        Java.type('dev.xfj.engine.scripting.ScriptGlue').rigidbody2DComponentApplyLinearImpulse(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), new InternalCalls.vector2f(point.x, point.y), wake);
    }
    static rigidbody2DComponentApplyLinearImpulseToCenter(entityId, impulse, wake) {
        Java.type('dev.xfj.engine.scripting.ScriptGlue').rigidbody2DComponentApplyLinearImpulseToCenter(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), wake);
    }
    static inputIsKeyDown(keyCode) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').inputIsKeyDown(keyCode);
    }
}