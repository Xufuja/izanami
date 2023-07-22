import Component from './component';
import InternalCalls from '../../internal-calls';

export default class TransformComponent extends Component {
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