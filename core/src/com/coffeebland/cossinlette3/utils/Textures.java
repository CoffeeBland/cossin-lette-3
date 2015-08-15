package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    public static final Texture WHITE_PIXEL;

    protected static final Map<String, SoftReference<Texture>> images = new HashMap<>();
    public static Texture get(String ref) {

        Texture texture;
        if (images.containsKey(ref)) {
            SoftReference<Texture> sref = images.get(ref);

            texture = sref.get();
            if (texture == null) {
                images.remove(ref);
                return get(ref);
            }
        } else {
            texture = new Texture(Gdx.files.internal("img/" + ref));
            images.put(ref, new SoftReference<>(texture));
        }

        return texture;
    }

    protected static final Map<String, TextureRegion[][]> tiles = new HashMap<>();
    public static TextureRegion[][] get(@NotNull TextureAtlas atlas, @NotNull String ref, int tileWidth, int tileHeight) {
        if (tiles.containsKey(ref)) {
            return tiles.get(ref);
        }

        TextureRegion[][] regions = atlas.findRegion(ref).split(tileWidth, tileHeight);

        tiles.put(ref, regions);

        return regions;
    }

    public static Texture getCheckeredTexture(int checkerSize, Color colorA, Color colorB) {
        Pixmap pixmap = new Pixmap(checkerSize * 2, checkerSize * 2, Pixmap.Format.RGB888);

        pixmap.setColor(colorA);
        pixmap.fillRectangle(0, 0, checkerSize, checkerSize);
        pixmap.fillRectangle(checkerSize, checkerSize, checkerSize, checkerSize);

        pixmap.setColor(colorB);
        pixmap.fillRectangle(checkerSize, 0, checkerSize, checkerSize);
        pixmap.fillRectangle(0, checkerSize, checkerSize, checkerSize);

        Texture texture = new Texture(pixmap);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        return texture;
    }

    public static void drawFilledRect(Batch batch, Color color, int x, int y, int w, int h) {
        Color previousColor = batch.getColor();
        batch.setColor(color);

        batch.draw(
                Textures.WHITE_PIXEL,
                x, y,
                w, h
        );

        batch.setColor(previousColor);
    }
    public static void drawRect(Batch batch, Color color, int x, int y, int w, int h, int thickness) {
        Color previousColor = batch.getColor();
        batch.setColor(color);

        batch.draw(
                Textures.WHITE_PIXEL,
                x, y,
                thickness, h
        );

        batch.draw(
                Textures.WHITE_PIXEL,
                x + w - thickness, y,
                thickness, h
        );

        batch.draw(
                Textures.WHITE_PIXEL,
                x, y,
                w, thickness
        );

        batch.draw(
                Textures.WHITE_PIXEL,
                x, y + h - thickness,
                w, thickness
        );

        batch.setColor(previousColor);
    }

    static {
        Pixmap whitePixelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixelPixmap.drawPixel(0, 0, 0xFFFFFFFF);
        WHITE_PIXEL = new Texture(whitePixelPixmap);
        whitePixelPixmap.dispose();
    }

    protected Textures() {}
}
