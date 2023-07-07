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

    multiply(scalar) {
        return new Vector3(this.#x * scalar, this.#y * scalar, this.#z * scalar);
    }
    add(other) {
        return new Vector3(this.#x + other.x, this.#y + other.y, this.#z + other.z);
    }

    get x() {
        return new this.#float(this.#x);
    }
    set x(x) {
        this.#x = x;
    }
    get y() {
        return new this.#float(this.#y);
    }
    set y(y) {
        this.#y = y;
    }
    get z() {
        return new this.#float(this.#z);
    }
    set z(z) {
        this.#z = z;
    }
    static zero() {
        return new Vector3(0.0, 0.0, 0.0);
    }
}

class InternalCalls {
    static vector2f = Java.type("org.joml.Vector2f");
    static vector3f = Java.type("org.joml.Vector3f");

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

class Input {
    static isKeyDown(keyCode) {
        return InternalCalls.inputIsKeyDown(keyCode);
    }
}

const KeyCode = {
    // From glfw3.h
    Space: 32,
    Apostrophe: 39, /* ' */
    Comma: 44, /* , */
    Minus: 45, /* - */
    Period: 46, /* . */
    Slash: 47, /* / */

    D0: 48, /* 0 */
    D1: 49, /* 1 */
    D2: 50, /* 2 */
    D3: 51, /* 3 */
    D4: 52, /* 4 */
    D5: 53, /* 5 */
    D6: 54, /* 6 */
    D7: 55, /* 7 */
    D8: 56, /* 8 */
    D9: 57, /* 9 */

    Semicolon: 59, /* ; */
    Equal: 61, /*: */

    A: 65,
    B: 66,
    C: 67,
    D: 68,
    E: 69,
    F: 70,
    G: 71,
    H: 72,
    I: 73,
    J: 74,
    K: 75,
    L: 76,
    M: 77,
    N: 78,
    O: 79,
    P: 80,
    Q: 81,
    R: 82,
    S: 83,
    T: 84,
    U: 85,
    V: 86,
    W: 87,
    X: 88,
    Y: 89,
    Z: 90,

    LeftBracket: 91,  /* [ */
    Backslash: 92,  /* \ */
    RightBracket: 93,  /* ] */
    GraveAccent: 96,  /* ` */

    World1: 161, /* non-US #1 */
    World2: 162, /* non-US #2 */

    /* Function keys */
    Escape: 256,
    Enter: 257,
    Tab: 258,
    Backspace: 259,
    Insert: 260,
    Delete: 261,
    Right: 262,
    Left: 263,
    Down: 264,
    Up: 265,
    PageUp: 266,
    PageDown: 267,
    Home: 268,
    End: 269,
    CapsLock: 280,
    ScrollLock: 281,
    NumLock: 282,
    PrintScreen: 283,
    Pause: 284,
    F1: 290,
    F2: 291,
    F3: 292,
    F4: 293,
    F5: 294,
    F6: 295,
    F7: 296,
    F8: 297,
    F9: 298,
    F10: 299,
    F11: 300,
    F12: 301,
    F13: 302,
    F14: 303,
    F15: 304,
    F16: 305,
    F17: 306,
    F18: 307,
    F19: 308,
    F20: 309,
    F21: 310,
    F22: 311,
    F23: 312,
    F24: 313,
    F25: 314,

    /* Keypad */
    KP0: 320,
    KP1: 321,
    KP2: 322,
    KP3: 323,
    KP4: 324,
    KP5: 325,
    KP6: 326,
    KP7: 327,
    KP8: 328,
    KP9: 329,
    KPDecimal: 330,
    KPDivide: 331,
    KPMultiply: 332,
    KPSubtract: 333,
    KPAdd: 334,
    KPEnter: 335,
    KPEqual: 336,

    LeftShift: 340,
    LeftControl: 341,
    LeftAlt: 342,
    LeftSuper: 343,
    RightShift: 344,
    RightControl: 345,
    RightAlt: 346,
    RightSuper: 347,
    Menu: 348
};

class Vector2 {
    #float = Java.type('java.lang.Float');
    #x;
    #y;

    constructor(x, y) {
        this.#x = x;
        this.#y = y;
    }
    multiply(scalar) {
        return new Vector2(this.#x * scalar, this.#y * scalar);
    }
    add(other) {
        return new Vector2(this.#x + other.x, this.#y + other.y);
    }

    get x() {
        return new this.#float(this.#x);
    }
    set x(x) {
        this.#x = x;
    }
    get y() {
        return new this.#float(this.#y);
    }
    set y(y) {
        this.#y = y;
    }
    static zero() {
    }
}

class Component {
    #entity;
    constructor(entity) {
        if (this.constructor == Component) {
            throw new Error("Component cannot be directly instantiated");
        }
        this.#entity = entity;
    }
    get entity() {
        return this.#entity;
    }
}

class TransformComponent extends Component {
    #translation;

    constructor(entity) {
        super(entity);
    }
    get translation() {
        this.#translation = InternalCalls.transformComponentGetTranslation(super.entity.id);
        return this.#translation;
    }
    set translation(value) {
        this.#translation = InternalCalls.transformComponentSetTranslation(super.entity.id, value);
    }
}

class Rigidbody2DComponent extends Component {
    constructor(entity) {
        super(entity);
    }
    applyLinearImpulse(impulse, worldPosition, wake) {
        InternalCalls.rigidbody2DComponentApplyLinearImpulse(super.entity.id, impulse, worldPosition, wake);
    }
    applyLinearImpulseToCenter(impulse, wake) {
        InternalCalls.rigidbody2DComponentApplyLinearImpulseToCenter(super.entity.id, impulse, wake);
    }
}

class Entity {
    #id;
    #translation;

    constructor(id = 0) {
        if (this.constructor == Entity) {
            throw new Error("Entity cannot be directly instantiated");
        }
        this.#id = id;
    }
    hasComponent(componentType) {
        return InternalCalls.entityHasComponent(this.#id, componentType);
    }
    getComponent(componentType) {
        if (this.hasComponent(componentType)) {
            return new componentType(this);
        } else {
            return null;
        }
    }
    get translation() {
        this.#translation = InternalCalls.transformComponentGetTranslation(this.#id);
        return this.#translation;
    }
    set translation(value) {
        this.#translation = InternalCalls.transformComponentSetTranslation(this.#id, value);
    }
    get id() {
        return this.#id;
    }
}

export { Component, Entity, Input, InternalCalls, KeyCode, Rigidbody2DComponent, TransformComponent, Vector2, Vector3 };
