const Entity = require('./engine/scene/Entity');

module.exports = class Camera extends Entity {
    constructor(id) {
        super(id);
    }
    onCreate() {
        console.log(`Camera.onCreate() - ${this.id}`);
    }
    onUpdate(ts) {
        let speed = 1.0;
        let velocity = Vector3.zero();

        if (Input.isKeyDown(KeyCode.Up)) {
            velocity.y = 1.0;
        }
        else if (Input.isKeyDown(KeyCode.Down)) {
            velocity.y = -1.0;
        }

        if (Input.isKeyDown(KeyCode.Left)) {
            velocity.x = -1.0;
        }
        else if (Input.isKeyDown(KeyCode.Right)) {
            velocity.x = 1.0;
        }

        velocity = velocity.multiply(speed);

        let translation = super.translation;
        translation = translation.add(velocity.multiply(ts));
        super.translation = translation;
    }
}