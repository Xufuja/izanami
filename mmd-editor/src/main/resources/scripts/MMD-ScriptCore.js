class Main {
    constructor() {
        console.log('Main constructor!')
    }
    printMessage() {
        console.log('Hello World from JavaScript!')
    }
    printInt(value) {
        console.log(`JavaScript says: ${value}`)
    }
    printInts(value1, value2) {
        console.log(`JavaScript says: ${value1} and ${value2}`)
    }
    printCustomMessage(message) {
        console.log(`JavaScript says: ${message}`)
    }
    log(text) {
        Java.type('dev.xfj.engine.core.Log').info(`${text}, and it works`);
    }
    getApplicationName() {
        var string = Java.type('dev.xfj.engine.core.application.Application').getApplication().getSpecification().name;
        console.log(string)
    }
}