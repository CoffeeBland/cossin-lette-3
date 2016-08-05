package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.coffeebland.cossinlette3.game.ui.DialogText;
import com.coffeebland.cossinlette3.game.ui.UIActor;
import com.coffeebland.cossinlette3.input.UpdateableInput;
import com.coffeebland.cossinlette3.utils.FontUtil;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.event.EventManager;
import com.coffeebland.cossinlette3.utils.event.Tag;

import java.util.*;

/**
 * Created by Guillaume on 2015-10-22.
 */
public class GameUI implements InputProcessor, UpdateableInput {

    @NtN protected EventManager eventManager;
    @NtN protected AssetManager assetManager;

    @N protected TextureAtlas uiAtlas;

    @NtN protected final List<UIActor> actors;
    @NtN protected Comparator<UIActor> comparator;

    public GameUI(
            @NtN EventManager eventManager,
            @NtN AssetManager assetManager
    ) {
        this.eventManager = eventManager;
        this.assetManager = assetManager;

        actors = new ArrayList<>();
        comparator = (lhs, rhs) -> Float.compare(lhs.getPriority(), rhs.getPriority());

        loadAssets();
    }

    protected void loadAssets() {
        String uiAtlasPath = "img/game/ui.atlas";
        eventManager.cancelTag(Tag.ASSETS);
        assetManager.load(uiAtlasPath, TextureAtlas.class);
        eventManager.post(Tag.ASSETS, () -> {
            uiAtlas = assetManager.get(uiAtlasPath);
            eventManager.runTag(Tag.UI_ATLAS);
        });
    }

    @NtN public List<UIActor> getActors() {
        return actors;
    }

    public void speak() {
        eventManager.post(Tag.UI_ATLAS, () -> {
            assert uiAtlas != null;
            getActors().add(new DialogText(
                    "Cossin",
                    FontUtil.roboto(),
                    400, 3,
                    new NinePatchDrawable(uiAtlas.createPatch("dialog")),
                    "Lorem ipsum dolor sit amet, [#ff8800]consectetur[] adipiscing elit. Integer sed metus ligula. " +
                    "In ut accumsan turpis.\n" +
                    "Phasellus hendrerit lobortis nulla eu euismod. Sed pharetra nisl " +
                    "dictum posuere malesuada. Aliquam lobortis finibus ex at egestas. Vivamus vel consectetur " +
                    "erat. Aliquam nec ullamcorper odio.\n" +
                    "\n" +
                    "Suspendisse congue leo non enim pretium, quis " +
                    "sagittis augue finibus. Fusce tincidunt, metus quis venenatis iaculis, dolor ligula " +
                    "porttitor sapien, ac tempor risus erat vel lacus. Pellentesque ut urna sit amet felis " +
                    "sollicitudin vehicula. Curabitur nec pharetra odio, et condimentum nulla. Duis vel magna " +
                    "diam. Sed id enim arcu. Aliquam erat volutpat. Cras porta auctor urna at ornare. Ut " +
                    "sodales tortor sed nulla finibus, sed maximus felis tincidunt.",
                    (count, next, previous) ->
                            (next != null && next == '\n') ||
                            (previous != null && previous == '\n') ?
                                    3600 : 60,
                    () -> {}
            ));
        });
    }

    public void resize(int width, int height) {}
    public void render(@NtN Batch batch) {
        batch.begin();

        Collections.sort(actors, comparator);
        for (UIActor actor: actors) actor.render(batch);

        batch.end();
    }
    public void update(float delta) {
        for (Iterator<UIActor> iterator = actors.iterator(); iterator.hasNext();) {
            UIActor actor = iterator.next();

            if (actor.shouldBeRemovedFromActors()) actor.removeFromUI(iterator);
            else actor.update(delta);
        }
    }
    @Override public boolean updateInput(float delta) {
        return actors
                .stream()
                .filter(a -> a.updateInput(delta))
                .findFirst()
                .isPresent();
    }
    @Override public boolean keyDown(int keycode) {
        return actors
                .stream()
                .filter(a -> a.keyDown(keycode))
                .findFirst()
                .isPresent();
    }
    @Override public boolean keyUp(int keycode) {
        return actors
                .stream()
                .filter(a -> a.keyUp(keycode))
                .findFirst()
                .isPresent();
    }
    @Override public boolean keyTyped(char character) {
        return actors
                .stream()
                .filter(a -> a.keyTyped(character))
                .findFirst()
                .isPresent();
    }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return actors
                .stream()
                .filter(a -> a.touchDown(screenX, screenY, pointer, button))
                .findFirst()
                .isPresent();
    }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return actors
                .stream()
                .filter(a -> a.touchUp(screenX, screenY, pointer, button))
                .findFirst()
                .isPresent();
    }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        return actors
                .stream()
                .filter(a -> a.touchDragged(screenX, screenY, pointer))
                .findFirst()
                .isPresent();
    }
    @Override public boolean mouseMoved(int screenX, int screenY) {
        return actors
                .stream()
                .filter(a -> a.mouseMoved(screenX, screenY))
                .findFirst()
                .isPresent();
    }
    @Override public boolean scrolled(int amount) {
        return actors
                .stream()
                .filter(a -> a.scrolled(amount))
                .findFirst()
                .isPresent();
    }
    public void dispose() {}
}
