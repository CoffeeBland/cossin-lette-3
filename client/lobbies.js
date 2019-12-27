import { get, sendForm } from './request';

export default function setupLobbies() {
    const lobbies = document.querySelector('#lobbies');
    const list = lobbies.querySelector('ul');
    const refresh = lobbies.querySelector('[name=refresh]');
    const createForm = lobbies.querySelector('form');
    let token;

    async function fetchLobbies() {
        refresh.disabled = 'disabled';
        list.innerHTML = '';
        try {
            const res = await fetch(`${process.env.SERVER_URL}/lobby`);
            if (res.status !== 200)
                return list.innerHTML = '<li><h3>:(</h3></li>';
            const json = await res.json();
            if (!json.length)
                return list.innerHTML = '<li><h3>Aucun lobby actif</h3></li>';
            json.forEach(({ name, users }) => {
                const li = document.createElement('li');
                const usersInfo =
                    users
                    .map(({ name: username }) => `<span>${ username }</span>`)
                    .join(', ');
                const btn = document.createElement('button');
                btn.onclick = () => console.log(name);
                btn.innerHTML = 'Rejoindre';

                li.innerHTML = `<h3>${ name }</h3><p>Genses: ${ usersInfo }</p>`;
                li.appendChild(btn);
                list.appendChild(li);
            });
        } catch (res) {
            console.warn(res);
            list.innerHTML = '<li><h3>:(</h3></li>';
        } finally {
            refresh.disabled = '';
        }
    }

    function onOpen(givenToken) {
        token = givenToken;
        fetchLobbies();
    }

    refresh.onclick = fetchLobbies;

    createForm.onsubmit = e => {
        e.preventDefault();
        sendForm('lobby', createForm).then(res => res && onLobbyJoined(res));
    }

    return {
        element: lobbies,
        onOpen
    };
}
