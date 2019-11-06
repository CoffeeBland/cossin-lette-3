const { performance } = require('perf_hooks');

const directional = Symbol('directional');
const press = Symbol('press');
const inputTypes = {
    move: directional,
    bubble: press,
    roll: press
};

class Game {
    constructor() {
        this.players = {
            register(id) {
                if (this[id]) return false;
                this[id] = new Map();
            },
            event(id, inputType, value) {
                this[id][inputType] = value;
            }
        };
        this.tickDur = 1000 / 60;
    }

    start() {
        this.resume();
    }

    stop() {
        this.pause();
    }

    resume() {
        if (this.timeoutId)
            return;
        this.then = performance.now();
        this.step();
    }

    pause() {
        clearTimeout(this.timeoutId);
        delete this.timeoutId;
    }

    step() {
        // Setup the next tick.
        const now = performance.now();
        const next = Math.max(this.tickDur - now - this.then, 0);
        this.then = now;
        this.timeoutId = setTimeout(this.step.bind(this), next);
    }
}

module.exports = Game;
