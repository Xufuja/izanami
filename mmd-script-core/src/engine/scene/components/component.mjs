export default class Component {
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