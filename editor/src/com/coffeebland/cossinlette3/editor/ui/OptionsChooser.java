package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.stream.Stream;

/**
 * Created by Guillaume on 2015-09-06.
 */
public class OptionsChooser extends Dialog {

    public static final TextField.TextFieldFilter
            NUMBER_FILTER = (textField, c) -> Character.isDigit(c),
            COLOR_FILTER = ((textField, c) -> "012345689abcdef".indexOf(Character.toLowerCase(c)) != -1);

    Label lblWidth, lblHeight,
            lblTileset, lblLayers,
            lblColor, lblColorPrefix;
    PropTextField txtWidth, txtHeight,
            txtLayers,
            txtColor;
    SelectBox<String> sltTileset;
    Button btnOk , btnCancel;
    @NotNull Listener listener;

    public OptionsChooser(@NotNull String title, @NotNull Skin skin, @NotNull Listener listener) {
        this(title, skin, null, listener);
    }
    public OptionsChooser(@NotNull String title, @NotNull Skin skin, @Nullable WorldDef defaults, @NotNull Listener listener) {
        super(title, skin);
        this.listener = listener;

        getContentTable().top().left();

        lblWidth = new Label("Largeur:", skin);
        lblWidth.setAlignment(Align.left);
        txtWidth = new PropTextField("15", skin);
        txtWidth.setTextFieldFilter(NUMBER_FILTER);
        txtWidth.setMaxLength(4);

        lblHeight = new Label("Hauteur:", skin);
        lblHeight.setAlignment(Align.left);
        txtHeight = new PropTextField("20", skin);
        txtHeight.setTextFieldFilter(NUMBER_FILTER);
        txtHeight.setMaxLength(4);

        lblTileset = new Label("Tileset:", skin);
        lblTileset.setAlignment(Align.left);
        sltTileset = new SelectBox<>(skin);
        sltTileset.setItems(
                Stream.of(new File("img/game").list((f, n) -> n.endsWith(".tileset.json")))
                        .map(n -> n.replace(".tileset.json", ""))
                        .toArray(String[]::new)
        );

        lblLayers = new Label("Couches:", skin);
        lblLayers.setAlignment(Align.left);
        txtLayers = new PropTextField("3", skin);
        txtLayers.setTextFieldFilter(NUMBER_FILTER);
        txtLayers.setMaxLength(1);

        lblColor = new Label("Couleur:", skin);
        lblColor.setAlignment(Align.left);
        lblColorPrefix = new Label("#", skin);
        lblColorPrefix.setAlignment(Align.right);
        lblColorPrefix.setColor(1, 1, 1, 0.5f);
        txtColor = new PropTextField("000000", skin);
        txtColor.setTextFieldFilter(COLOR_FILTER);
        txtColor.setMaxLength(6);

        if (defaults != null) {
            txtWidth.setText(String.valueOf(defaults.width));
            txtHeight.setText(String.valueOf(defaults.height));
            sltTileset.setSelected(defaults.imgSrc);
            txtLayers.setText(String.valueOf(defaults.tileLayers.size()));
            txtColor.setText(defaults.backgroundColor.toString().substring(0, 6));
        }

        btnOk = new TextButton("Ok", skin);
        button(btnOk, true);
        key(Keys.ENTER, true);

        btnCancel = new TextButton("Annuler", skin);
        button(btnCancel, false);
        key(Keys.ESCAPE, false);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Table content = getContentTable();

        content.pad(8);

        content.row();
        content.add(lblWidth);
        content.add(txtWidth);
        content.add(lblHeight);
        content.add(txtHeight);

        content.row();
        content.add(lblTileset);
        content.add(sltTileset).fillX().expandX().colspan(3);

        content.row();
        content.add(lblLayers);
        content.add(txtLayers).fillX().expandX().colspan(3);

        content.row().align(Align.topLeft);
        content.add(lblColor);
        content.add(lblColorPrefix).fillX().expandX();
        content.add(txtColor);
        content.add(new Widget() {
            @Override public float getPrefWidth() { return txtColor.getPrefHeight(); }
            @Override public float getPrefHeight() { return txtColor.getPrefHeight(); }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                if (txtColor.getText().length() != 6) return;
                Color color = Color.valueOf(txtColor.getText() + "ff");
                Textures.drawFilledRect(batch, color, getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);
                color.a = 0.5f;
                Textures.drawFilledRect(batch, color, getX(), getY(), getWidth(), getHeight());
            }
        });

        return super.show(stage, action);
    }

    @Override
    protected void result(Object object) {
        boolean result = (boolean)object;
        if (result) {
            try {
                int width = Integer.parseInt(txtWidth.getText());
                int height = Integer.parseInt(txtHeight.getText());
                String tileset = sltTileset.getSelected();
                int layerCount = Integer.parseInt(txtLayers.getText());
                if (!(width > 0 && height > 0 && layerCount > 0)) cancel();
                else listener.result(width, height, tileset, layerCount, Color.valueOf(txtColor.getText() + "ff"));
            } catch (NumberFormatException ex) {
                cancel();
            }
        }
    }

    public interface Listener {
        void result(int width, int height, String tileset, int layerCount, Color color);
    }
}
