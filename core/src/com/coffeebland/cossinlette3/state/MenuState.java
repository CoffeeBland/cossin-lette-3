package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.coffeebland.cossinlette3.utils.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuState extends StateImpl<Void> {

    protected float time = 0, target = 1;

    protected int selection = 0;

    protected CenterCroppedSprite
            layerBG,
            layerA,
            layerB,
            layerC,
            layerD,
            layerE,
            layerF,
            layerG,
            layerH;

    protected RatioedSprite
            title,
            load,
            start,
            quit,
            selectArrow;

    @Override
    public void onPrepare(Void nil, StateManager.Notifier notifier) {
        super.onPrepare(nil, notifier);

        layerBG = new CenterCroppedSprite("main_screen/couche_bg.png", 10);
        layerA = new CenterCroppedSprite("main_screen/couche_a.png", 98);
        layerB = new CenterCroppedSprite("main_screen/couche_b.png", 60);
        layerC = new CenterCroppedSprite("main_screen/couche_c.png", 48);
        layerD = new CenterCroppedSprite("main_screen/couche_d.png", 36);
        layerE = new CenterCroppedSprite("main_screen/couche_e.png", 30);
        layerF = new CenterCroppedSprite("main_screen/couche_f.png", 28);
        layerG = new CenterCroppedSprite("main_screen/couche_g.png", 24);
        layerH = new CenterCroppedSprite("main_screen/couche_h.png", 20);
        title = new RatioedSprite("main_screen/couche_titre.png", (1108f * 354f) / (2400f * 1350f));
        load = new RatioedSprite("main_screen/menu_charger.png", (44f * 225f) / (2400f * 1350f));
        start = new RatioedSprite("main_screen/menu_demarrer.png", (44f * 266f) / (2400f * 1350f));
        quit = new RatioedSprite("main_screen/menu_quitter.png", (44f * 204f) / (2400f * 1350f));
        selectArrow = new RatioedSprite("main_screen/menu_fleche.png", (43f * 50f) / (2400f * 1350f));

        eventManager.post(Tag.ASSETS, notifier::prepared);
    }

    @Override public void render(@NotNull Batch batch) {
        batch.begin();

        layerBG.render(batch, time);
        layerH.render(batch, time);
        layerG.render(batch, time);
        layerF.render(batch, time);
        layerE.render(batch, time);
        layerD.render(batch, time);
        layerC.render(batch, time);
        layerB.render(batch, time);
        layerA.render(batch, time);

        float ratio = title.getRatio();
        title.x = ratio * 62; // distance in the image between the title's lines
        title.y = Gdx.graphics.getHeight() - title.getHeight(ratio) - ratio * 62;
        title.render(batch, time);

        float startRatio = start.getRatio();
        float loadRatio = load.getRatio();
        float quitRatio = quit.getRatio();
        float selectRatio = selectArrow.getRatio();

        start.x = Gdx.graphics.getWidth() - start.getWidth(startRatio) - selectArrow.getWidth(selectRatio) - 1.5f * startRatio * 44;
        start.y = quit.getHeight(quitRatio) * 2 + load.getHeight(loadRatio) * 2 + start.getHeight(startRatio);
        start.render(batch, time);

        load.render(batch, time);
        load.x = Gdx.graphics.getWidth() - load.getWidth(loadRatio) - selectArrow.getWidth(selectRatio) - 1.5f * loadRatio * 44;
        load.y = quit.getHeight(quitRatio) * 2 + load.getHeight(loadRatio);

        quit.render(batch, time);
        quit.x = Gdx.graphics.getWidth()  - quit.getWidth(quitRatio) - selectArrow.getWidth(selectRatio) - 1.5f * quitRatio * 44;
        quit.y = quit.getHeight(quitRatio);

        selectArrow.x = Gdx.graphics.getWidth() - selectArrow.getWidth(selectRatio) - startRatio * 44;
        switch (selection) {
            case 0: selectArrow.y = start.y + (start.getHeight(startRatio) - selectArrow.getHeight(selectRatio)) / 2; break;
            case 1: selectArrow.y = load.y + (load.getHeight(loadRatio) - selectArrow.getHeight(selectRatio)) / 2; break;
            case 2: selectArrow.y = quit.y + (quit.getHeight(quitRatio) - selectArrow.getHeight(selectRatio)) / 2; break;
        }
        selectArrow.render(batch, time);

        batch.end();
    }

    @Override public void update(float delta) {
        super.update(delta);

        target = 1 - selection / 3f;
        time = time * 0.99f + target * 0.01f;
    }

    @Override public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                selection -= 1;
                while (selection < 0) selection += 3;
                return true;
            case Input.Keys.DOWN:
                selection = (selection + 1) % 3;
                return true;
            case Input.Keys.ENTER:
            case Input.Keys.SPACE:
                switch (selection) {
                    case 0:
                        new StateManager.TransitionArgs<>(GameState.class)
                                .setLength(TRANSITION_LONG, TRANSITION_LONG)
                                .beginSwitch(stateManager);
                        break;
                    case 1:
                        new StateManager.TransitionArgs<>(LoadFileState.class)
                                .setLength(TRANSITION_SHORT, TRANSITION_SHORT)
                                .beginSwitch(stateManager);
                        break;
                    case 2:
                        Gdx.app.exit();
                        break;
                }
                return true;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                return true;
        }
        return false;
    }

    public class RatioedSprite extends MenuSprite {
        float targetAreaRatio;

        public RatioedSprite(String src, float targetAreaRatio) {
            super(src);
            this.targetAreaRatio = targetAreaRatio;
        }

        public float getRatio() {
            if (texture == null) return 0;
            int area = Gdx.graphics.getWidth() * Gdx.graphics.getHeight();
            int textureArea = texture.getWidth() * texture.getHeight();
            return (float)Math.sqrt(targetAreaRatio / (textureArea / (float)area));
        }
        public int getWidth() {
            return getWidth(getRatio());
        }
        public int getWidth(float ratio) {
            return texture == null ? 0 : (int)(texture.getWidth() * ratio);
        }
        public int getHeight() {
            return getHeight(getRatio());
        }
        public int getHeight(float ratio) {
            return texture == null ? 0 : (int)(texture.getHeight() * ratio);
        }

        @Override public void render(@NotNull Batch batch, float interpolated) {
            if (texture == null) return;

            // we have (s * tW) * (s * tH) / (sW * sH) = ratio
            // <=> s² * ((tW * tH) / (sw * sH)) = ratio
            // <=> s = root(ratio / ((tW * tH) / (sW * sH)))

            float finalRatio = getRatio();
            batch.draw(texture, x, y, (int)(texture.getWidth() * finalRatio), (int)(texture.getHeight() * finalRatio));
        }
    }
    public class CenterCroppedSprite extends MenuSprite {
        float decal;
        public CenterCroppedSprite(String src, float decal) {
            super(src);
            this.decal = decal;
        }

        @Override public void render(@NotNull Batch batch, float interpolated) {
            if (texture == null) return;

            y = decal * (1 - interpolated);

            int availWidth = (int)(Gdx.graphics.getWidth() + decal);
            int availHeight = (int)(Gdx.graphics.getHeight() + decal);
            float ratioX =  (float)texture.getWidth() / (float)availWidth;
            float ratioY = (float)texture.getHeight() / (float)availHeight;
            float ratio = Math.min(ratioX, ratioY);
            int ratioedWidth = (int)(availWidth * ratio);
            int ratioedHeight = (int)(availHeight * ratio);
            batch.draw(texture,
                    x - decal, y - decal, // pos
                    availWidth, availHeight, // size
                    (texture.getWidth() - ratioedWidth) / 2, (texture.getHeight() - ratioedHeight) / 2, // origin pos
                    ratioedWidth, ratioedHeight, // origin size
                    false, false
            );
        }
    }
    public class MenuSprite {
        @Nullable public Texture texture;
        public float x, y;

        public MenuSprite(@NotNull String src) {
            loadTextxure(src);
        }
        void loadTextxure(@NotNull String src) {
            load("img/" + src, Texture.class, img -> texture = img);
        }

        public void render(@NotNull Batch batch, float interpolated) {
            if (texture == null) return;

            batch.draw(texture, x, y);
        }
    }
}