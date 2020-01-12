// Load env
require('dotenv').config();

// Monkey patches
require('../monkey');

const express = require('express');
const http = require('http');
require('express-async-errors');
const app = express();
const server = http.createServer(app);

// Setup middlewares

// TODO setup cors properly (partly, parcel's doing its' own retard thing)
const cors = require('cors');
app.use(cors());

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// Routes setup
app.use(require('./user').router);
app.use('/lobby', require('./lobby').router);

// Error handling
app.use((err, req, res, next) => {
    if (typeof err === 'string') {
        return res.status(400).send({ error: err });
    }
    console.error(err.stack);
    res.status(500).send('Whoops!');
});

// For dev, setup parcel for the client
if (process.env.NODE_ENV !== 'production') {
    console.log('Setting up parcel');
    const path = require('path');
    const Bundler = require('parcel-bundler');
    const bundler = new Bundler(path.resolve(__dirname, '../index.html'));
    app.use(bundler.middleware());
}

// Sockets setup
const Game = require('../game');
const rtc = require('./rtc');
rtc.io.addServer(server);

// Finalize setup
console.log('Listening on port:', process.env.SERVER_PORT);
server.listen(process.env.SERVER_PORT);
