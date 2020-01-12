const express = require('express');
const strs = require('./strs');
const { users, auth } = require('./user');
const formatters = require('./formatters');
const rtc = require('./rtc');

const lobbies = {
    _backing: [
        {
            name: 'Ce lobby',
            users: [{ name: "Banna" }, { name: "Fabs" }],
            roomId: 'lobby_Ce lobby'
        },
        {
            name: 'YÉYÉYÉYÉ',
            users: [{ name: "Georges" }],
            roomId: 'lobby_YÉYÉYÉYÉ'
        }
    ],
    list: async function() {
        return lobbies._backing;
    },
    forName: async function(name) {
        return lobbies._backing.find(lobby => lobby.name === name);
    },
    addToLobby: async function(user, lobby) {
        if (user.lobby === lobby)
            return;
        user.lobby && await lobbies.removeFromLobby(user);
        user.lobby = lobby;
        user.chan && user.chan.join(lobby.roomId);
        lobby.users.push(user);
    },
    removeFromLobby: async function(user, lobby = user.lobby) {
        if (!lobby.users.includes(user))
            return;
        delete user.lobby;
        lobby.users.remove(user);
        user.chan && user.chan.leave(lobby.roomId);
        if (!lobby.users.length)
            await lobbies.destroy(lobby);
    },
    create: async function(name) {
        const lobby = { name, users: [], roomId: `lobby_${name}` };
        this._backing.push(lobby);
        return lobby;
    },
    destroy: async function(lobby) {
        lobbies._backing.remove(lobby);
    },
    sendMessage: async function(lobby, message) {
        rtc.io.room(lobby.roomId).emit('message', formatters.message(message));
    }
};

rtc.emitter.on(rtc.events.Register, user =>
    user.lobby && user.chan.join(user.lobby.roomId));

rtc.emitter.on(rtc.events.Disconnect, user =>
    user.lobby && user.chan.leave(user.lobby.roomId));

const router = express.Router();

router.use(auth);

router.get('/', async (_, res) =>
    res.status(200).json(
        (await lobbies.list()).map(formatters.lobby)));

router.post('/', async ({ user, body: { name = '' } }, res) => {
    // TODO strip message
    name = name.trim();
    const errs = [];

    if (!name.length) errs.push(strs.name_len);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    const lobby = await lobbies.create(name);
    await lobbies.addToLobby(user, lobby);
    return res.status(200).json(formatters.lobby(lobby));
});

router.use(async (req, res, next) => {
    const name = (req.body.name || '').trim();
    const errs = [];

    const lobby = await lobbies.forName(name);
    if (!lobby) errs.push(strs.bad_lobby);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    req.lobby = lobby;
    next();
});

router.post('/leave', async ({ user, lobby }, res) => {
    await lobbies.removeFromLobby(user, lobby);
    return res.status(200).json({});
});

router.post('/join', async ({ user, lobby }, res) => {
    await lobbies.addToLobby(user, lobby);
    return res.status(200).json(formatters.lobby(lobby));
});

router.post('/message', async ({ user, lobby, body: { message }}, res) => {
    // TODO strip message
    const errs = [];
    if (!message) errs.push(strs.msg_len);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    const data = { sender: user, body: message };
    lobbies.sendMessage(lobby, data);
    return res.status(200).json(formatters.message(data));
});

module.exports = { lobbies, router };
