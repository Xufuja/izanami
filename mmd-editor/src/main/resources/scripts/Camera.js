const Entity = require('./engine/scene/Entity');

module.exports = class Camera extends Entity {
    constructor() {
        super();
    }
    onUpdate(ts) {
        let speed = 1.0;
        let velocity = Vector3.zero;

        if (Input.IsKeyDown(KeyCode.Up))
            velocity.Y = 1.0;
        else if (Input.IsKeyDown(KeyCode.Down))
            velocity.Y = -1.0;

        if (Input.IsKeyDown(KeyCode.Left))
            velocity.X = -1.0;
        else if (Input.IsKeyDown(KeyCode.Right))
            velocity.X = 1.0;

        velocity *= speed;

        let translation = Translation;
        translation += velocity * ts;
        Translation = translation;
    }
}