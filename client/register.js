import { sendForm } from './request';

export default function setupRegister({ onRegistered }) {
    const register = document.querySelector('#register');
    register.onsubmit = e => {
        e.preventDefault();
        sendForm(register).then(res => res && onRegistered(res));
    };

    function displayMessage(msg = '') {
        register.querySelector('.danger').innerText = msg;
    }

    return {
        element: register,
        onOpen: displayMessage
    };
}
