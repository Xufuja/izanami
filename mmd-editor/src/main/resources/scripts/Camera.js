const Entity = require('./engine/scene/Entity');

module.exports = class Camera extends Entity {
    constructor() {
        super();
    }
    onUpdate(ts) {
        let speed = 1.0;
        let velocity = Vector3.zero();

        if (Input.isKeyDown(KeyCode.Up))
            velocity.y = 1.0;
        else if (Input.isKeyDown(KeyCode.Down))
            velocity.y = -1.0;

        if (Input.isKeyDown(KeyCode.Left))
            velocity.x = -1.0;
        else if (Input.isKeyDown(KeyCode.Right))
            velocity.x = 1.0;

        velocity.x *= speed;
        velocity.y *= speed;
        velocity.z *= speed;

        let translation = Translation;
        translation += velocity * ts;
        Translation = translation;
    }
}