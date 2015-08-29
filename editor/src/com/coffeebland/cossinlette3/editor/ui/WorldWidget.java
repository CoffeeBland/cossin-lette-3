package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldWidget extends Widget {

    @NotNull protected Tileset tileset;
    @Nullable protected WorldDef worldDef;
    @Nullable protected List<TileLayer> tileLayers;
    @NotNull protected Vector2 cameraPos = VPool.V2();
    @NotNull protected KeyInputListener keyInputListener;
    @Nullable protected TileTool tileTool;
    protected float cameraSpeed = 5f;

    protected final Rectangle widgetAreaBounds = new Rectangle();
    protected final Rectangle scissorBounds = new Rectangle();

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

    public void resetToWorldDef(@Nullable WorldDef def) {
        worldDef = def;
        cameraPos.setZero();

        if (worldDef != null) {
            tileLayers = new ArrayList<>(worldDef.tileLayers.size());
            for (TileLayerDef tileLayerDef : worldDef.tileLayers) {
                tileLayers.add(new TileLayer(tileLayerDef, tileset));
            }
        } else {
            tileLayers = null;
        }
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        widgetAreaBounds.set(getX(), getY(), getWidth(), getHeight());
        getStage().calculateScissors(widgetAreaBounds, scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {

            if (worldDef != null) {
                assert tileLayers != null;

                Textures.drawRect(batch,
                        Color.WHITE,
                        (int) (getX() - Dst.getAsPixels(cameraPos.x)),
                        (int) (getY() - Dst.getAsPixels(cameraPos.y)),
                        worldDef.width * tileset.getTileSizePixels(),
                        worldDef.height * tileset.getTileSizePixels(),
                        1
                );

                Vector2 offsetPos = VPool.V2(cameraPos).sub(Dst.getAsMeters(getX()), Dst.getAsMeters(getY()));
                for (TileLayer tileLayer : tileLayers) {
                    for (TileLayer.Row row : tileLayer.getRows()) {
                        row.render(batch, offsetPos);
                    }
                }
                VPool.claim(offsetPos);

                if (tileTool != null) {
                }
            }

            batch.flush();
            ScissorStack.popScissors();
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        keyInputListener.updateInputs(delta);
    }

    public static class TileTool {
        double width, height;
        @Nullable WorldDef worldDef;

        public TileTool() {

        }

        public void setWorldDef(@Nullable WorldDef def) {
            worldDef = def;
        }

        public void render(Batch batch, float x, float y) {

        }
    }
}
