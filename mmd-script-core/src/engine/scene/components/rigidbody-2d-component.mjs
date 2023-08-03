import Component from './component';
import InternalCalls from '../../internal-calls';

export default class Rigidbody2DComponent extends Component {
    #type

    constructor(entity) {
        super(entity);
    }
    linearVelocity() {
        return InternalCalls.rigidbody2DComponentGetType(super.entity.id, Vector2.zero()); 
    }
    get type() {
        this.#type = InternalCalls.rigidbody2DComponentGetType(super.entity.id);
        return this.#type;
    }
    set type(value) {
        this.#type = InternalCalls.rigidbody2DComponentSetType(super.entity.id, value);
    }
    applyLinearImpulse(impulse, worldPosition, wake) {
        InternalCalls.rigidbody2DComponentApplyLinearImpulse(super.entity.id, impulse, worldPosition, wake);
    }
    applyLinearImpulseToCenter(impulse, wake) {
        InternalCalls.rigidbody2DComponentApplyLinearImpulseToCenter(super.entity.id, impulse, wake);
    }
}