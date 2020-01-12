const { default: geckos } = require('@geckos.io/server');
const EventEmitter = require('events');

module.exports = {
    io: geckos({
        iceServers: [],
        port: process.env.RTC_PORT
    }),

    emitter: new EventEmitter(),

    events: {
        Register: Symbol('rtc_register'),
        Disconnect: Symbol('rtc_disconnect')
    }
};
