package com.coffeebland.cossinlette3.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.game.GameCamera;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.file.WorldFile;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.coffeebland.cossinlette3.utils.Const.METERS_PER_PIXEL;

public class TileLayer extends Actor {

    public static final int TAG_ANIM = -1;

    @Nullable protected Texture texture;
    @NotNull public WorldFile.TileLayerDef def;
    protected float cumulatedSeconds = 0;

    public TileLayer(@NotNull WorldFile.TileLayerDef def) {
        super(def);

        this.def = def;
    }

    @Nullable public Texture getTexture() { return texture; }
    public int getTextureTilesX() {
        return texture == null ? 0 : (texture.getWidth() / def.tileSize);
    }
    public int getTextureTilesY() {
        return texture == null ? 0 : (texture.getHeight() / def.tileSize);
    }

    @Override public void addToWorld(@NotNull GameWorld world) {
        super.addToWorld(world);
        texture = Textures.get(def.src);
    }

    @Override public void removeFromWorld() {
        super.removeFromWorld();
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override public void render(@NotNull SpriteBatch batch, @NotNull GameCamera camera) {
        for (int y = 0; y < def.height; y++) {
            int[][] row = def.tiles[y];
            for (int x = 0; x < def.width; x++) {
                int[] tile = row[x];
                for (int i = 0; i < tile.length; i += 2) {
                    int first = tile[i], second = tile[i + 1];
                    int tileX, tileY;

                    if (first == TAG_ANIM) {
                        int[] anim = def.animations[second];
                        int frameCount = anim[0];
                        int fps = anim[1];

                        int frameOffset = (int)((cumulatedSeconds * fps) % frameCount) + 1;
                        tileX = anim[frameOffset * 2];
                        tileY = anim[frameOffset * 2 + 1];

                        i += frameCount * 2;
                    } else {
                        tileX = first;
                        tileY = second;
                    }

                    batch.draw(texture,
                            (def.x - camera.getPos().x) / METERS_PER_PIXEL + x * def.tileSize,
                            (def.y - camera.getPos().y) / METERS_PER_PIXEL + y * def.tileSize,
                            tileX * def.tileSize,
                            tileY * def.tileSize,
                            def.tileSize,
                            def.tileSize
                    );
                }
            }
        }
    }

    @Override public void update(float delta) {
        cumulatedSeconds = (cumulatedSeconds + delta / 1000) % 1000;
    }
}
