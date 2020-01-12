import { get, rtc, sendForm } from './request';

export default function setupLobby({ onLobbyLeft }) {
    const lobby = document.querySelector('#lobby');
    const title = lobby.querySelector('h2');
    const usersList = lobby.querySelector('.users');
    const messagesList = lobby.querySelector('.messages');
    const messageForm = lobby.querySelector('.message');
    const leaveForm = lobby.querySelector('.leave');
    const nameFields = lobby.querySelectorAll('[name=name]');
    const timeouts = [];

    function connect({ name, users }) {
        title.innerText = name;
        nameFields.forEach(field => field.value = name);
        usersList.innerText = users.map(({ name }) => name).join(', ');
    }

    function disconnect() {
        timeouts.forEach(clearTimeout);
        timeouts.length = 0;
        messagesList.innerHTML = '';
    }

    function onError(error) {
        const li = document.createElement('li');
        li.classList.add('danger');
        li.innerText = error;
        messagesList.appendChild(li);
        const timeoutId = setTimeout(() => {
            li.remove();
            timeouts.remove(timeoutId);
        }, 3000);
        timeouts.push(timeoutId);
    }

    rtc.on('message', ({ sender, body }) => {
        const li = document.createElement('li');
        const senderNode = document.createElement('em');
        senderNode.innerText = sender + ' : ';
        li.appendChild(senderNode);
        const bodyNode = document.createTextNode(body);
        li.appendChild(bodyNode);
        messagesList.appendChild(li);
    });

    messageForm.onsubmit = e => {
        e.preventDefault();
        sendForm(messageForm, { onError })
        .then(res => res && messageForm.reset());
    };

    leaveForm.onsubmit = e => {
        e.preventDefault();
        sendForm(leaveForm, { onError })
        .then(res => res && onLobbyLeft(res));
    };

    return {
        element: lobby,
        onOpen: connect,
        onClose: disconnect
    };
}
