import '../monkey';
import setupRegister from './register';
//import setupGame from './game';
import setupLobbies from './lobbies';
import setupLobby from './lobby';
import { setToken, setupRequest } from './request';

let current;
function show(state, ...args) {
    if (current === state)
        return;
    current && current.element.classList.remove('show');
    current && current.onClose && current.onClose();
    state.element.classList.add('show');
    state.onOpen && state.onOpen(...args);
    current = state;
}

const states = {
    register: setupRegister({
        onRegistered: async ({ token, name, lobby }) => {
            setToken(token);
            if (lobby) show(states.lobby, lobby);
            else show(states.lobbies);
        }
    }),
    lobbies: setupLobbies({
        onLobbyJoined: lobby => show(states.lobby, lobby)
    }),
    lobby: setupLobby({
        onLobbyLeft: () => show(states.lobbies)
    }),
    //game: setupGame()
};

setupRequest(msg => show(states.register, msg));
show(states.register);
