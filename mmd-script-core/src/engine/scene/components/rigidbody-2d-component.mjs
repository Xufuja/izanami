import Component from './component';
import InternalCalls from '../../internal-calls';

export default class Rigidbody2DComponent extends Component {
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