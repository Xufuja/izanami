const Entity = require('./engine/scene/Entity');

module.exports = class Player extends Entity {
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
        this.#rigidbody.applyLinearImpulse(velocity, velocity, true);

        //let translation = this.#transform.translation;
        //translation = translation.add(velocity.multiply(ts));
        //this.#transform.translation = translation;

    }
}