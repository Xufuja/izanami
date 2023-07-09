class Camera extends Entity {
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

class Player extends Entity {
    #transform;
    #rigidbody;

    speed = 0.01;
    time = 0.0;

    constructor(id) {
        super(id);
    }
    onCreate() {
        console.log(`Player.onCreate() - ${this.id}`);
        this.#transform = super.getComponent(TransformComponent);
        this.#rigidbody = super.getComponent(Rigidbody2DComponent);
    }
    onUpdate(ts) {
        this.time += ts;
        //console.log(`Player.onUpdate(): ${this.#time}`);

        let speed = this.speed;
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

export { Camera, Player };
