package com.coffeebland.cossinlette3;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.Actor;
import com.coffeebland.cossinlette3.state.GameState;
import com.coffeebland.cossinlette3.utils.FontUtil;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class DialogText extends DialogInteraction {

    protected BitmapFont font;

    @NtN protected Source<String> textSource;

    @NtN protected String text;
    protected int displayedLetters;

    public DialogText(
            @N Action nextAction,
            @NtN Source<Actor> actorSource,
            @NtN Source<String> displayNameSource,
            @NtN Source<String> textSource) {
        super(nextAction, actorSource, displayNameSource);
        this.font = FontUtil.pixel(16);
        this.textSource = textSource;
        text = "...";
    }

    @Override
    public void execute(@NtN GameState state, @NtN GameWorld world, @NtN Context ctx) {
        super.execute(state, world, ctx);
        String text = textSource.eval(state, world, ctx);
        this.text = text != null ? text : "...";
    }

    @Override
    public boolean update(float delta) {
        boolean superVal = super.update(delta);

        displayedLetters++;

        return superVal;
    }

    @Override public void renderContent(@NtN Batch batch, @NtN Vector2 pos, @NtN Vector2 size) {
        font.draw(batch, text, 0, 0, 0, 0, 0, 0, true);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}