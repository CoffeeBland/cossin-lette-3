package com.coffeebland.cossinlette3.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.V2;

/**
 * Created by Guillaume on 2015-10-12.
 */
public abstract class DialogInteraction extends UIActor {

    @NtN protected Vector2 contentSize;
    @NtN protected Drawable window;
    @NtN protected String displayName;
    @N Listener listener;

    public DialogInteraction(
            @NtN String displayName,
            @NtN Vector2 size,
            @NtN Drawable window,
            @N Listener listener
    ) {
        super(PRIORITY_DIALOG);
        this.displayName = displayName;
        this.contentSize = size;
        this.window = window;
        this.listener = listener;
    }

    public void renderWindow(@NtN Batch batch, @NtN Vector2 pos, @NtN Vector2 size) {
        window.draw(batch, pos.x, pos.y, size.x, size.y);
    }
    public abstract void renderContent(@NtN Batch batch, @NtN Vector2 pos, @NtN Vector2 size);
    public void render(@NtN Batch batch) {
        Vector2 pos = V2.get(),
                globalSize = V2.get(contentSize),
                contentShift = V2.get();

        globalSize.add(
                window.getLeftWidth() + window.getRightWidth(),
                window.getBottomHeight() + window.getTopHeight()
        );
        contentShift.add(window.getLeftWidth(), window.getBottomHeight());
        pos.add(Gdx.graphics.getWidth(), 0).sub(globalSize.x, 0).scl(0.5f);

        renderWindow(batch, pos, globalSize);
        renderContent(batch, pos.add(contentShift), contentSize);
        V2.claim(pos, globalSize, contentShift);
    }

    public void close() {
        if (listener != null) listener.onClosed();
        flagForRemoval();
    }

    @Override public boolean updateInput(float delta) { return true; }
    @Override public boolean keyDown(int keycode) { return true; }
    @Override public boolean keyUp(int keycode) { return true; }
    @Override public boolean keyTyped(char character) { return true; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(int amount) { return true; }

    public interface Listener {
        void onClosed();
    }
}
