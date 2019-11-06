import setupRegister from './register';
import setupGame from './game';

let rootElements;

function show(element) {
    rootElements.forEach(e => e.classList.add('hidden'));
    element.classList.remove('hidden');
}

const register = setupRegister(async (token) => {
    const res = await fetch('state')
    console.log('connected');
});

const game = setupGame();

rootElements = [register.element, game.element];
show(register.element);
