import Vector3 from './vector3';

export default class InternalCalls {
    static vector2f = Java.type("org.joml.Vector2f");
    static vector3f = Java.type("org.joml.Vector3f");

    static entityHasComponent(entityId, componentType) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').entityHasComponent(entityId, InternalCalls.getComponentType(componentType));
    }
    static entityFindEntityByName(name) {
        return Java.type('dev.xfj.engine.scripting.ScriptGlue').entityFindEntityByName(name);
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