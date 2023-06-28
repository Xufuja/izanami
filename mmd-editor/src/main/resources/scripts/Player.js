module.exports = class Player extends Entity {
    constructor() {
        super();
    }
    onCreate() {
        console.log('Created!');
    }
    onUpdate() {
        console.log('Updated!');
    }
}