import { get, sendForm } from './request';

export default function setupLobbies({ onLobbyJoined }) {
    const lobbies = document.querySelector('#lobbies');
    const list = lobbies.querySelector('ul');
    const refresh = lobbies.querySelector('[name=refresh]');
    const createForm = lobbies.querySelector('form');

    async function fetchLobbies() {
        refresh.disabled = 'disabled';
        list.innerHTML = '';
        try {
            const res = await get('lobby');
            if (res.status !== 200)
                return list.innerHTML = '<li><h3>:(</h3></li>';
            const json = await res.json();
            if (!json.length)
                return list.innerHTML = '<li><h3>Aucun lobby actif</h3></li>';
            json.forEach(({ name, users }) => {
                const li = document.createElement('li');
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'lobby/join';
                // TODO : Le texte devrait être escapé
                form.innerHTML = `
                    <h3>${ name }</h3>
                    <p>Genses: ${users
                        .map(({ name }) => `<span>${ name }</span>`)
                        .join(', ') }
                        <span class="danger"></span>
                    </p>
                    <input type="hidden" name="name" value="${ name }" />
                    <button type="submit">Rejoindre</button>`;
                form.onsubmit = e => {
                    e.preventDefault();
                    sendForm(form).then(res => res && onLobbyJoined(res));
                };
                li.appendChild(form);
                list.appendChild(li);
            });
        } catch (res) {
            console.warn(res);
            list.innerHTML = '<li><h3>:(</h3></li>';
        } finally {
            refresh.disabled = '';
        }
    }

    refresh.onclick = fetchLobbies;

    createForm.onsubmit = e => {
        e.preventDefault();
        sendForm(createForm).then(res => res && onLobbyJoined(res));
    };

    return {
        element: lobbies,
        onOpen: fetchLobbies
    };
}
