module.exports = class Entity {
    #id;
    translation;

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
            switch (componentType) {
                case TransformComponent:
                    return new TransformComponent(this);

                case Rigidbody2DComponent:
                    return new Rigidbody2DComponent(this);
                default:
                    console.log("Invalid component type!");
            }
        } else {
            return null;
        }
    }
    get id() {
        return this.#id;
    }
}