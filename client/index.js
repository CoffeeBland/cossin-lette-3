import setupRegister from './register';
import setupGame from './game';
import setupLobbies from './lobbies';
import { setToken } from './request';

let current;
function show(state) {
    if (current === state)
        return;
    current && current.element.classList.remove('show');
    current && current.onClose && current.onClose();
    state.element.classList.add('show');
    state.onOpen && state.onOpen();
    current = state;
}

const states = {
    register: setupRegister(async ({ token: newToken, lobby }) => {
        setToken(newToken);
        if (lobby) show(states.lobby);
        else show(states.lobbies);
    }),
    lobbies: setupLobbies(),
    game: setupGame()
};

show(states.register);
