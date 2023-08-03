import Vector3 from './vector3';
import BodyType from './scene/components/body-type';

export default class InternalCalls {
    static packageName = "dev.xfj.engine";
    static vector2f = Java.type("org.joml.Vector2f");
    static vector3f = Java.type("org.joml.Vector3f");

    static entityHasComponent(entityId, componentType) {
        return Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).entityHasComponent(entityId, InternalCalls.getComponentType(componentType));
    }
    static entityFindEntityByName(name) {
        return Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).entityFindEntityByName(name);
    }
    static getScriptInstance(entityId) {
        return Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).getScriptInstance(entityId);
    }
    static getComponentType(componentType) {
        return Java.type(`${InternalCalls.packageName}.scene.components.${componentType.name}`);
    }
    static transformComponentGetTranslation(entityId) {
        let vec3 = Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).transformComponentGetTranslation(entityId);
        return new Vector3(vec3.x, vec3.y, vec3.z);
    }
    static transformComponentSetTranslation(entityId, translation) {
        let vec3 = Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).transformComponentSetTranslation(entityId, new InternalCalls.vector3f(translation.x, translation.y, translation.z));
        return new Vector3(vec3.x, vec3.y, vec3.z);
    }
    static rigidbody2DComponentApplyLinearImpulse(entityId, impulse, point, wake) {
        Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).rigidbody2DComponentApplyLinearImpulse(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), new InternalCalls.vector2f(point.x, point.y), wake);
    }
    static rigidbody2DComponentGetLinearVelocity(entityId, linearVelocity) {
        let vec2 = Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).rigidbody2DComponentGetLinearVelocity(entityId, new InternalCalls.vector2f(linearVelocity.x, linearVelocity.y));
        return new Vector3(vec2.x, vec2.y);
    }
    static rigidbody2DComponentGetType(entityId) {
        let type = Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).rigidbody2DComponentGetType(entityId);
        return new BodyType(type);
    }
    static rigidbody2DComponentSetType(entityId, bodyType) {
        let type = Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).rigidbody2DComponentSetType(entityId, bodyType);
        return new BodyType(type);
    }
    static rigidbody2DComponentApplyLinearImpulseToCenter(entityId, impulse, wake) {
        Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).rigidbody2DComponentApplyLinearImpulseToCenter(entityId, new InternalCalls.vector2f(impulse.x, impulse.y), wake);
    }
    static inputIsKeyDown(keyCode) {
        return Java.type(`${InternalCalls.packageName}.scripting.ScriptGlue`).inputIsKeyDown(keyCode);
    }
}