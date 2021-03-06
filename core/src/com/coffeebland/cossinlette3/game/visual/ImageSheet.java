package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.Textures;

public class ImageSheet {
    @NtN public final String src;
    @NtN public final TextureRegion[][] textures;
    public final int frameWidth, frameHeight, framesX, framesY, decalX, decalY;

    public ImageSheet(@NtN TextureAtlas atlas, @NtN String src, int frameWidth, int frameHeight, int decalX, int decalY) {
        this.src = src;
        this.textures = Textures.get(atlas, src, frameWidth, frameHeight);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesX = textures[0].length;
        this.framesY = textures.length;
        this.decalX = decalX;
        this.decalY = decalY;
    }

    public void render(@NtN Batch batch, float x, float y, int imageX, int imageY, float scale, boolean flip) {
        batch.draw(
                textures[imageY][imageX],
                x - decalX, y - decalY,
                decalX, decalY,
                frameWidth, frameHeight,
                (flip ? -1 : 1) * scale, scale, 0
        );
    }
    public void render(@NtN Batch batch, float x, float y, int imageX, int imageY, float scale, boolean flip, @NtN Color tint) {
        batch.setColor(tint);
        render(batch, x, y, imageX, imageY, scale, flip);
        batch.setColor(Color.WHITE);
    }
}
