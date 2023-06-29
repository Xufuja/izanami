module.exports = class Player extends Entity {
    constructor(entity) {
        super();
    }
    onCreate() {
        console.log('Created!');
    }
    onUpdate(ts) {
        console.log(`TimeStep: ${ts}`);
    }
}