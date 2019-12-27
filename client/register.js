import { sendForm } from './request';

export default function setupRegister(onRegistered) {
    const register = document.querySelector('#register');
    register.onsubmit = e => {
        e.preventDefault();
        sendForm('register', register).then(res => res && onRegistered(res));
    };

    return { element: register };
}
