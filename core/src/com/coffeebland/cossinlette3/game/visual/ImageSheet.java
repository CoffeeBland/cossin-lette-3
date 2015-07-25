package com.coffeebland.cossinlette3.game.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.file.ImageSheetDef;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;

public class ImageSheet {
    @NotNull public final String src;
    @NotNull public final Texture texture;
    public final int frameWidth, frameHeight, framesX, framesY, decalX, decalY;

    public ImageSheet(@NotNull String src, int frameWidth, int frameHeight, int decalX, int decalY) {
        this.src = src;
        this.texture = Textures.get(src);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesX = texture.getWidth() / frameWidth;
        this.framesY = texture.getHeight() / frameHeight;
        this.decalX = decalX;
        this.decalY = decalY;
    }
    public ImageSheet(ImageSheetDef def) {
        this(def.src, def.frameWidth, def.frameHeight, def.decalX, def.decalY);
    }

    public void render(@NotNull SpriteBatch batch, float x, float y, int imageX, int imageY, float scale, boolean flip) {
        batch.draw(texture,
                x - decalX, y - decalY,
                frameWidth * scale, frameHeight * scale,
                frameWidth * imageX,
                frameHeight * imageY,
                frameWidth, frameHeight,
                flip, false
        );
    }
    public void render(@NotNull SpriteBatch batch, float x, float y, int imageX, int imageY, float scale, boolean flip, @NotNull Color tint) {
        batch.setColor(tint);
        render(batch, x, y, imageX, imageY, scale, flip);
        batch.setColor(Color.WHITE);
    }
}
