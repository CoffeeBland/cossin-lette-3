import geckos, { Data }  from '@geckos.io/client';
import { Application, Sprite } from 'pixi.js';

export default function setupGame() {
    const canvas = document.querySelector('#game');
    const app = new Application({ view: canvas, resizeTo: canvas });
    app.loader
    .add('cossinMarche', 'cossin-marche.png')
    .add('cossinNeutre', 'cossin-neutre.png')
    .load((loader, res) => {
        console.log(res);
        const test = new Sprite(res.cossinNeutre.texture);

        app.stage.addChild(test);
    });

    /*const chan = geckos({ port: 1444 });
    chan.onConnect(() => {
        console.log('connected');
        chan.on('chat message', console.log);
    });*/

    return { element: canvas };
}
