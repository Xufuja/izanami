export default class Camera extends Entity {
    #player;

    eOtherEntity = null;
    fDistanceFromPlayer  = 5.0;

    constructor(id) {
        super(id);
    }
    onCreate() {
        this.#player = super.findEntityByName("Player");
    }
    onUpdate(ts) {
        if (this.#player) {
            super.translation = new Vector3(this.#player.translation.x, this.#player.translation.y, this.fDistanceFromPlayer);
        }

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