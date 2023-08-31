export default class Vector3 {
    #float = Java.type('java.lang.Float');
    #x;
    #y;
    #z;

    constructor(x, y, z) {
        this.#x = x;
        this.#y = y;
        this.#z = z;
    }

    multiply(scalar) {
        return new Vector3(this.#x * scalar, this.#y * scalar, this.#z * scalar);
    }
    add(other) {
        return new Vector3(this.#x + other.x, this.#y + other.y, this.#z + other.z);
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
    get z() {
        return new this.#float(this.#z);
    }
    set z(z) {
        this.#z = z;
    }
    static zero() {
        return new Vector3(0.0, 0.0, 0.0);
    }
}