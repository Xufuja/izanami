import InternalCalls from '../internal-calls';

export default class Entity {
    #id;
    #translation;

    constructor(id = 0) {
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
    findEntityByName(name) {
        let entityId = InternalCalls.entityFindEntityByName(name);
        
        if (entityId == 0) {
            return null;
        }

        return new Entity(entityId);
    }
    //No need to cast in JavaScript so instead of specifying the Entity subclass to cast to, just calling it asEntity()
    asEntity() {
        return InternalCalls.getScriptInstance(this.#id);
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