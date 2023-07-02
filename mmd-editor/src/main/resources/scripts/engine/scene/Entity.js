module.exports = class Entity {
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