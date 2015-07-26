package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.editor.EditorState;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;

public abstract class ClickTool {

    protected EditorState state;
    protected ClickListener mainListener;
    protected ClickListener secondaryListener;

    public ClickTool(EditorState state) {
        this.state = state;
        mainListener = new ClickListener(Input.Buttons.LEFT) {
            @Override public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = VPool.V2(x, y);
                handlePrimaryClick(transformCoordinates(pos));
                VPool.claim(pos);
            }
        };
        secondaryListener = new ClickListener(Input.Buttons.RIGHT) {
            @Override public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = VPool.V2(x, y);
                handleSecondaryClick(transformCoordinates(pos));
                VPool.claim(pos);
            }
        };
    }

    public void attach(Widget gameWorldWidget) {
        gameWorldWidget.addListener(mainListener);
        gameWorldWidget.addListener(secondaryListener);
    }
    public void detach(Widget gameWorldWidget) {
        gameWorldWidget.removeListener(mainListener);
        gameWorldWidget.removeListener(secondaryListener);
    }

    public void render(Batch batch) {
        Vector2 pos = VPool.V2(Gdx.input.getX(), Gdx.input.getY());
        render(batch, transformCoordinates(pos));
        VPool.claim(pos);
    }
    @NotNull public abstract Vector2 transformCoordinates(@NotNull Vector2 pos);
    public abstract void render(Batch batch, @NotNull Vector2 pos);
    public abstract void handlePrimaryClick(@NotNull Vector2 pos);
    public abstract void handleSecondaryClick(@NotNull Vector2 pos);
}
