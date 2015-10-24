package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.HashMap;
import java.util.Map;

public class FontUtil {

    protected static Map<String, FreeTypeFontGenerator> fontGenerators = new HashMap<>();

    @NtN public static BitmapFont font(String name, int size) {
        FreeTypeFontGenerator ftfg = fontGenerators.containsKey(name) ? fontGenerators.get(name) : null;
        if (ftfg == null) fontGenerators.put(name, ftfg = new FreeTypeFontGenerator(Gdx.files.internal("font/" + name)));

        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;

        return ftfg.generateFont(parameter);
    }
    @NtN public static BitmapFont pixel(int size) {
        return font("pixel.ttf", size);
    }
    @NtN public static BitmapFont roboto(int size) {
        return font("roboto-medium.ttf", size);
    }

    @NtN public static BitmapFont pixel() {
        return pixel(18);
    }
    @NtN public static BitmapFont roboto() {
        return roboto(18);
    }
}
