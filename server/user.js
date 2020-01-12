const express = require('express');
const strs = require('./strs');
const uuid = require('uuid/v4');
const formatters = require('./formatters');
const rtc = require('./rtc');

const users = {
    _backing: [],
    forName: async name => users._backing.find(u => u.name === name),
    forToken: async token => users._backing.find(u => u.token === token),
    create: async (name, pass) => {
        const created = { name, pass, token: uuid(), chans: null };
        users._backing.push(created);
        return created;
    },
    auth: async (user, pass) => {
        if (user.pass != pass) {
            throw strs.name_dup;
        }
        user.last = new Date();
        return user;
    }
};

rtc.io.onConnection(chan => {
    chan.emit('register');
    chan.on('register', async token => {
        const user = await users.forToken(token);
        if (!user)
            return chan.emit('disconnect', strs.bad_token);
        if (user.chan === chan)
            return;

        if (user.chan) {
            user.chan.emit('disconnect', strs.chan_dup);
            rtc.emitter.emit(rtc.events.Disconnect, user);
        }

        user.chan = chan;
        rtc.emitter.emit(rtc.events.Register, user);
    });

    chan.onDisconnect(() => {
        if (!chan.user)
            return;

        rtc.emitter.emit(rtc.events.Disconnect, chan.user);
        delete chan.user.chan;
    });
});

const router = express.Router();
router.post('/register', async ({ body: { name = '', pass = '' }}, res) => {
    // TODO: strip name
    name = name.trim();
    const errs = [];
    if (!name.length) errs.push(strs.name_len);
    if (pass.length < 4) errs.push(strs.mdp_len);
    if (errs.length)
        return res.status(400).json({ error: errs.join('\n') });

    const user = await users.forName(name) || await users.create(name, pass);
    const authedUser = await users.auth(user, pass);
    res.status(200).send(formatters.currentUser(authedUser));
});

async function auth(req, res, next) {
    err: {
        if (!req.headers.authorization)
            break err;
        const [bearer, token] = req.headers.authorization.split(' ');
        if (bearer !== 'Bearer')
            break err;
        const user = await users.forToken(token);
        if (!user)
            break err;
        req.user = user;
        return next();
    }
    return res.status(401).json({ error: strs.bad_token });
}

module.exports = { users, router, auth };
