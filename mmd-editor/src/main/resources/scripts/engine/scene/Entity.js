module.exports = class Entity {
    #id;
    translation;

    constructor(id = 0) {
        if (this.constructor == Entity) {
            throw new Error("Entity cannot be directly instantiated");
        }
        this.#id = id;
        console.log(this.#id)
    }
    hasComponent(componentType) {
        return true;
    }
    getComponent(componentType) {
        return true;
    }
}