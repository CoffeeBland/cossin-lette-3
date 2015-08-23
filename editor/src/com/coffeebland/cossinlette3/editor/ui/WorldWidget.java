package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldWidget extends Widget {



    @NotNull protected Tileset tileset;
    @Nullable protected WorldDef worldDef;
    @NotNull protected Vector2 cameraPos = VPool.V2();
    @NotNull protected KeyInputListener keyInputListener;
    protected float cameraSpeed = 5f;

    public WorldWidget(@NotNull Tileset tileset) {
        this.tileset = tileset;
        keyInputListener = new KeyInputListener(Keys.LEFT, Keys.UP, Keys.RIGHT, Keys.DOWN) {
            @Override public void onInputDown(int keyCode) {}
            @Override public void onInputUpdate(int keyCode, long pressTime, float delta) {
                switch (keyCode) {
                    case Input.Keys.LEFT:
                        cameraPos.x -= cameraSpeed * delta / 1000;
                        break;

                    case Input.Keys.UP:
                        cameraPos.y += cameraSpeed * delta / 1000;
                        break;

                    case Input.Keys.RIGHT:
                        cameraPos.x += cameraSpeed * delta / 1000;
                        break;

                    case Input.Keys.DOWN:
                        cameraPos.y -= cameraSpeed * delta / 1000;
                        break;
                }
            }
            @Override public void onInputUp(int keyCode, long pressTime) {}
        };
        addListener(keyInputListener);
    }

    public void setWorldDef(@Nullable WorldDef worldDef) {
        this.worldDef = worldDef;
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (worldDef != null) {
            Textures.drawRect(batch,
                    Color.WHITE,
                    (int)(getX() - Dst.getAsPixels(cameraPos.x)),
                    (int)(getY() - Dst.getAsPixels(cameraPos.y)),
                    worldDef.width * tileset.getTileSizePixels(),
                    worldDef.height * tileset.getTileSizePixels(),
                    1
            );
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        keyInputListener.updateInputs(delta);
    }

    public void resetToWorldDef(@Nullable WorldDef def) {
        worldDef = def;
        cameraPos.setZero();
    }
}
