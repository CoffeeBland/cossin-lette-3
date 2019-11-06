require('dotenv').config();
const http = require('http');
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const uuid = require('uuid/v4');
const { default: geckos, Data, Channel } = require('@geckos.io/server');
const Game = require('../game');

if (Array.prototype.remove)
    console.warn('Overriding existing function!');
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
app.locals.users = new Map();
app.locals.lobbies = [];

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
    let user = Array.from(users.values()).find(u => u.name === name);
    if (!user) {
        user = { name, pass, token: uuid() };
        users.set(user.token, user);
    } else if (user.pass !== pass) {
        return res.status(400).json({ error: strs.name_dup });
    }

    user.last = new Date();
    res.status(200).send({
        token: user.token,
        lobby: user.lobby
    });
});

app.get('lobby', (_, res) =>
    res.status(200).json(
        app.locals.lobbies.map(lobby => ({
            name: lobby.name,
            users: lobby.users.length
        }))));

addToLobby(user, lobby) {
    user.lobby = lobby.name;
    lobby.users.push(user);
}

removeFromLobby(user, lobby) {
    delete user.lobby;
    lobby.users.remove(user);
}

app.post('lobby', ({ body: { token = '', name = '' }}, res) => {
    name = name.trim();
    const { lobbies, users } = app.locals;
    const errs = [];

    if (!name.length) errs.push(strs.name_len);
    if (!token.length || !users[token]) errs.push(strs.bad_token);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    let lobby = lobbies[name];
    if (!lobby) {
        lobby = { name, users: [] };
        lobbies.set(name, lobby);
    }
    const user = users[token];
    user.lobby && removeFromLobby(user, lobbies[user.lobby]);
    addToLobby(user, lobby);
});

app.post('lobby/leave', ({ body: { token = '', name = '' }}, res) => {
    name = name.trim();
    const { lobbies, users } = app.locals;
    const errs = [];

    if (!token.length || !users[token])
        errs.push(strs.bad_token);
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
