package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by Guillaume on 2015-09-22.
 */
public class PropTextField extends TextField {

    public PropTextField(String text, Skin skin) {
        super(text, skin);
    }

    public PropTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public PropTextField(String text, TextFieldStyle style) {
        super(text, style);
    }

    @Override
    public float getPrefWidth() {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0, n = getMaxLength(); i < n; i++) tmp.append("#");
        GlyphLayout glyphLayout = new GlyphLayout(getStyle().font, tmp);
        return glyphLayout.width + getStyle().background.getLeftWidth() + getStyle().background.getRightWidth();
    }
}
