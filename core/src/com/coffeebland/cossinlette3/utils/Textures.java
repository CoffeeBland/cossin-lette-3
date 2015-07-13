package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    public static final Texture WHITE_PIXEL;

    private static final Map<String, SoftReference<Texture>> images = new HashMap<>();
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

    static {
        Pixmap whitePixelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixelPixmap.drawPixel(0, 0, 0xFFFFFFFF);
        WHITE_PIXEL = new Texture(whitePixelPixmap);
        whitePixelPixmap.dispose();
    }

    private Textures() {}
}
