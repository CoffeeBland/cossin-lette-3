require('dotenv').config();
const http = require('http');
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const uuid = require('uuid/v4');
const { default: geckos, Data, Channel } = require('@geckos.io/server');
const Game = require('../game');

Array.prototype.remove && console.warn('Overriding existing function!');
Array.prototype.remove = function(e) {
    const i = this.indexOf(e);
    if (i === -1)
        return false;
    this.splice(i, 1);
    return true;
}

const app = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(cors());
app.locals.users = {};
app.locals.lobbies = {};

// TODO remove
app.locals.lobbies['Ce lobby'] = {
    name: 'Ce lobby',
    users: [{ name: "Banna" }, { name: "Fabs" }]
};
app.locals.lobbies['YÉYÉYÉYÉ'] = {
    name: 'YÉYÉYÉYÉ',
    users: [{ name: "Georges" }]
};

const strs = {
    mdp_len: 'Mot de passe doit être au moins 4 caractères',
    name_len: 'Nom nécessaire',
    name_dup: 'Nom déjà utilisé',
    bad_token: 'Utilisateur inexistant',
    bad_lobby: 'Lobby inexistant'
}

app.post('/register', ({ body: { name = '', pass = '' }}, res) => {
    name = name.trim();
    const errs = [];
    if (!name.length) errs.push(strs.name_len);
    if (pass.length < 4) errs.push(strs.mdp_len);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    const users = app.locals.users;
    let user = Object.values(users).find(u => u.name === name);
    if (!user) {
        user = { name, pass, token: uuid() };
        users[user.token] = user;
    } else if (user.pass !== pass) {
        return res.status(400).json({ error: strs.name_dup });
    }

    user.last = new Date();
    res.status(200).send({
        token: user.token,
        lobby: user.lobby
    });
});

app.get('/lobby', (_, res) =>
    res.status(200).json(
        Object.values(app.locals.lobbies).map(lobby => ({
            name: lobby.name,
            users: lobby.users.map(u => ({ name: u.name }))
        }))));

function addToLobby(user, lobby) {
    user.lobby = lobby.name;
    lobby.users.push(user);
}

function removeFromLobby(user, lobby) {
    delete user.lobby;
    lobby.users.remove(user);
}

app.post('/lobby', ({ body: { token = '', name = '' }}, res) => {
    name = name.trim();
    const { lobbies, users } = app.locals;
    const errs = [];

    if (!token.length || !users[token])
        return res.status(401).json({ error: strs.bad_token });
    if (!name.length) errs.push(strs.name_len);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    const lobby = lobbies[name] || (lobbies[name] = { name, users: [] });
    const user = users[token];
    user.lobby && removeFromLobby(user, lobbies[user.lobby]);
    addToLobby(user, lobby);
});

app.post('/lobby/leave', ({ body: { token = '' }}, res) => {
    name = name.trim();
    const { lobbies, users } = app.locals;
    const errs = [];

    if (!token.length || !users[token])
        return res.status(401).json({ error: strs.bad_token });
    if (!name.length || !lobbies[name])
        errs.push(strs.bad_lobby);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    let lobby = lobbies[name];
});

const io = geckos();

io.onConnection(chan => {
    /*console.log('conn', chan.id);
    chan.on('register', id => game.players.register(id));
    chan.onDisconnect((), id => game.players.unregister(id));
    chan.on('chat message', console.log);*/
});

const server = http.createServer(app);
io.addServer(server);
console.log('Listening on port:', process.env.SERVER_PORT);
server.listen(process.env.SERVER_PORT);
