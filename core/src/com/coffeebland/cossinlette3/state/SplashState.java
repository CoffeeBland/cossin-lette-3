package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.coffeebland.cossinlette3.utils.FontUtil;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.event.Tag;

import static com.coffeebland.cossinlette3.utils.Const.PADDING;

public class SplashState extends AutoSwitchState {

    @Override protected long getDuration() { return 2000; }
    @Override protected Class<? extends StateImpl> getNextStateClass() { return MenuState.class; }
    @Override protected Color getTransitionColor() { return new Color(0xFFFFFFFF); }
    @Override protected float getTransitionDuration() { return TRANSITION_LONG; }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
            case Input.Keys.ENTER:
            case Input.Keys.ESCAPE:
                switchEvent.remaining = 0;
                return true;
        }
        return false;
    }

    protected BitmapFont font;
    protected Texture bg;
    protected Texture logo;
    protected GlyphLayout text;

    public SplashState() {
        setBackgroundColor(Color.LIGHT_GRAY.cpy());

        font = FontUtil.pixel();
        bg = Textures.WHITE_PIXEL;
        text = new GlyphLayout(font, "Catiniata - Dagothig");
    }

    @Override
    public void onPrepare(Void nil, StateManager.Notifier notifier) {
        super.onPrepare(nil, notifier);
        load("img/ui/coffeebland.png", Texture.class, (img) -> logo = img);
        eventManager.post(Tag.ASSETS, notifier::prepared);
    }

    @Override
    public void render(@NtN Batch batch) {
        batch.begin();

        float imgX = (Gdx.graphics.getWidth() / 2) - (logo.getWidth() / 2);
        float imgY = (Gdx.graphics.getHeight() / 2) - (logo.getHeight() / 2);

        batch.setColor(new Color(0x75858AFF));
        batch.draw(bg, PADDING, PADDING, Gdx.graphics.getWidth() - PADDING * 2, Gdx.graphics.getHeight() - PADDING * 2);
        batch.setColor(Color.WHITE.cpy());
        batch.draw(logo, imgX, imgY);
        font.draw(batch, text, (Gdx.graphics.getWidth() / 2) - (text.width / 2), PADDING * 4);

        batch.end();
    }
}