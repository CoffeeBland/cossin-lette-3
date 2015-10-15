package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Textures {
    public static final Texture WHITE_PIXEL;

    protected static final Map<String, TextureRegion[][]> tiles = new HashMap<>();
    public static TextureRegion[][] get(@NotNull TextureAtlas atlas, @NotNull String ref, int tileWidth, int tileHeight) {
        if (tiles.containsKey(ref)) {
            return tiles.get(ref);
        }

        TextureRegion[][] regions = atlas.findRegion(ref).split(tileWidth, tileHeight);

        tiles.put(ref, regions);

        return regions;
    }

    public static void drawFilledRect(@NotNull Batch batch, @NotNull Color color, int x, int y, int w, int h) {
        Color previousColor = batch.getColor();
        batch.setColor(color);

        batch.draw(
                Textures.WHITE_PIXEL,
                x, y,
                w, h
        );

        batch.setColor(previousColor);
    }
    public static void drawFilledRect(@NotNull Batch batch, @NotNull Color color, float x, float y, float w, float h) {
        drawFilledRect(batch, color,
                (int) Math.floor(x), (int) Math.floor(y),
                (int) Math.floor(w), (int) Math.floor(h)
        );
    }
    public static void drawFilledRect(@NotNull Batch batch, @NotNull Color color, @NotNull Vector2 pos, float w, float h) {
        drawFilledRect(batch, color, pos.x, pos.y, w, h);
    }
    public static void drawFilledRect(@NotNull Batch batch, @NotNull Color color, @NotNull Vector2 pos, @NotNull Vector2 size) {
        drawFilledRect(batch, color, pos.x, pos.y, size.x, size.y);
    }
    public static void drawRect(@NotNull Batch batch, @NotNull Color color, int x, int y, int w, int h, int thickness) {
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
    public static void drawRect(@NotNull Batch batch, @NotNull Color color, float x, float y, float w, float h, int thickness) {
        drawRect(batch, color,
                (int) Math.floor(x),
                (int) Math.floor(y),
                (int) Math.floor(w),
                (int) Math.floor(h),
                thickness
        );
    }
    public static void drawRect(@NotNull Batch batch, @NotNull Color color, @NotNull Vector2 pos, float w, float h, int thickness) {
        drawRect(batch, color, pos.x, pos.y, w, h, thickness);
    }
    public static void drawRect(@NotNull Batch batch, @NotNull Color color, @NotNull Vector2 pos, @NotNull Vector2 size, int thickness) {
        drawRect(batch, color, pos.x, pos.y, size.x, size.y, thickness);
    }

    static {
        Pixmap whitePixelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixelPixmap.drawPixel(0, 0, 0xFFFFFFFF);
        WHITE_PIXEL = new Texture(whitePixelPixmap);
        whitePixelPixmap.dispose();
    }

    protected Textures() {}
}
