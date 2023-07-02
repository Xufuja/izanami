const Entity = require('./engine/scene/Entity');

module.exports = class Player extends Entity {
    #transform;
    #rigidbody;

    constructor(id) {
        super(id);
        console.log('Main constructor!');
        this.logText("Test", 1);

        let pos = new Vector3(5, 2.5, 1);
        let result = this.logVector(pos);
        console.log(`${result.x}, ${result.y}, ${result.z}`);
        console.log(`${InternalCalls.logVectorDot(pos)}`);
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
    printMessage() {
        console.log('Hello World from JavaScript!');
    }
    printInt(value) {
        console.log(`JavaScript says: ${value}`);
    }
    printInts(value1, value2) {
        console.log(`JavaScript says: ${value1} and ${value2}`);
    }
    printCustomMessage(message) {
        console.log(`JavaScript says: ${message}`);
    }
    logText(text, parameter) {
        InternalCalls.logText(text, parameter);
    }
    logVector(parameter) {
        return InternalCalls.logVector(parameter);
    }
}