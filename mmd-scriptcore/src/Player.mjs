import Entity from './engine/scene/Entity';
import Input from './engine/Input';
import KeyCode from './engine/KeyCode';
import Vector3 from './engine/Vector3';
import TransformComponent from './engine/scene/components/TransformComponent';
import Rigidbody2DComponent from './engine/scene/components/Rigidbody2DComponent';

export default class Player extends Entity {
    #transform;
    #rigidbody;

    constructor(id) {
        super(id);
    }
    onCreate() {
        console.log(`Player.onCreate() - ${this.id}`);
        this.#transform = super.getComponent(TransformComponent);
        this.#rigidbody = super.getComponent(Rigidbody2DComponent);
    }
    onUpdate(ts) {
        //console.log(`Player.onUpdate(): ${ts}`);

        let speed = 0.01;
        let velocity = Vector3.zero();

        if (Input.isKeyDown(KeyCode.W)) {
            velocity.y = 1.0;
        }
        else if (Input.isKeyDown(KeyCode.S)) {
            velocity.y = -1.0;
        }

        if (Input.isKeyDown(KeyCode.A)) {
            velocity.x = -1.0;
        }
        else if (Input.isKeyDown(KeyCode.D)) {
            velocity.x = 1.0;
        }

        velocity = velocity.multiply(speed);

        //Center version does not exist so testing like this
        if (this.#rigidbody) {
            this.#rigidbody.applyLinearImpulse(velocity, velocity, true);
        }

        //let translation = this.#transform.translation;
        //translation = translation.add(velocity.multiply(ts));
        //console.log(`X: ${translation.x}\r\nY: ${translation.y}\r\nZ: ${translation.z}`);
        //this.#transform.translation = translation;

    }
}