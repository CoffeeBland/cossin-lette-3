let token;

export function get() {
    return fetch.apply(this, arguments);
}

export async function sendForm(url, form, values = {}) {
    const submit = form.querySelector('[type=submit]');
    const danger = form.querySelector('.danger');
    token && (values.token = token);

    submit.disabled = 'disabled';
    danger.innerText = '\n';
    try {
        const res = await fetch(`${process.env.SERVER_URL}/${url}`, {
            method: register.method,
            body: JSON.stringify(Array
                .from(register.elements)
                .filter(e => e.name && e.value)
                .reduce((obj, e) => (obj[e.name] = e.value, obj), values)),
            headers: { 'content-type': 'Application/JSON' }
        });
        if (res.status === 200)
            return await res.json();
        else if (res.status === 400)
            danger.innerText = (await res.json()).error;
        else
            danger.innerText = "Une erreur s'est produite";
    }
    catch (res) {
        console.warn(res);
        danger.innerText = "Une erreur s'est produite";
    } finally {
        submit.disabled = '';
    }
}

export function setToken(givenToken) {
    token = givenToken;
}
