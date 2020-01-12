import geckos, { Data } from '@geckos.io/client';

let token, onAuthError;

const rtc = geckos({
    port: process.env.RTC_PORT
});
rtc.onConnect(error => {
    // TODO this should be handled somehow
    if (error)
        console.error(error);
});
rtc.on('register', () => token && rtc.emit('register', token));
rtc.on('disconnect', err => onAuthError(err));

const headers = () => ({
    'authorization': token && 'Bearer ' + token,
    'content-type': 'Application/JSON'
});

export function get(path) {
    // TODO handle 401
    return fetch(path, { method: 'GET', headers: headers() });
}

export async function sendForm(form, { values = {}, onError } = {}) {
    const submit = form.querySelector('[type=submit]');
    const danger = form.querySelector('.danger');
    const displayErr =
        onError ||
        (danger && (err => danger.innerText = err)) ||
        alert;

    submit.disabled = 'disabled';
    danger && (danger.innerText = '');

    try {
        const res = await fetch(form.action, {
            method: form.method,
            body: JSON.stringify(Array
                .from(form.elements)
                .filter(e => e.name && e.value)
                .reduce((obj, e) => (obj[e.name] = e.value, obj), values)),
            headers: headers()
        });
        const json = await res.json();
        if (res.status === 200)
            return json;
        else if (res.status === 400)
            displayErr(json.error);
        else if (res.status === 401)
            onAuthError(json.error);
        else
            displayErr("Une erreur s'est produite");
    }
    catch (res) {
        console.warn(res);
        displayErr("Une erreur s'est produite");
    } finally {
        submit.disabled = '';
    }
}

export function setToken(givenToken) {
    token = givenToken;
    rtc.onConnect(() => rtc.emit('register', token));
}

export function setupRequest(givenOnAuthError) {
    onAuthError = givenOnAuthError;
}

export { rtc };
