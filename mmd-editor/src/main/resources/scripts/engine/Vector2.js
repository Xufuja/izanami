module.exports = class Vector2 {
    #float = Java.type('java.lang.Float');
    #x;
    #y;

    constructor(x, y) {
        this.#x = x;
        this.#y = y;
    }
    get x() {
        return new this.#float(this.#x);
    }
    set x(x) {
        this.#x = x;
    }
    get y() {
        return new this.#float(this.#y);
    }
    set y(y) {
        this.#y = y;
    }
    static zero() {
        new Vector2(0, 0);
    }
}