export default function setupRegister(onRegistered) {
    const register = document.querySelector('#register');
    const registerError = register.querySelector('.danger');

    async function sendForm() {
        try  {
            const res = await fetch(`${process.env.SERVER_URL}/register`, {
                method: register.method,
                body: JSON.stringify(Array
                    .from(register.elements)
                    .filter(e => e.name)
                    .reduce((obj, e) => (obj[e.name] = e.value, obj), {})),
                headers: { 'content-type': 'Application/JSON' }
            });
            if (res.status === 200)
                await onRegistered((await res.json()).token);
            else if (res.status === 400)
                registerError.innerText = (await res.json()).error;
            else
                registerError.innerText = "Une erreur s'est produite";
        }
        catch (res) {
            console.warn(res);
            registerError.innerText = "Une erreur s'est produite";
        }
    }

    register.onsubmit = e => {
        e.preventDefault();
        registerError.innerText = '\n';
        sendForm();
    };

    return { element: register };
}
