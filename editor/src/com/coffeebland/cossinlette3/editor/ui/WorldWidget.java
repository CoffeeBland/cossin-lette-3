package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.tools.TileTool;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldWidget extends Widget implements EventListener {

    @NotNull protected Tileset tileset;
    @NotNull protected TileLayerSource tileLayerSource;
    @NotNull protected OperationExecutor operationExecutor;
    @Nullable protected WorldDef worldDef;
    @Nullable protected List<TileLayer> tileLayers;
    @NotNull protected Vector2 cameraPos = V2.get();
    @NotNull protected KeyInputListener keyInputListener;
    @Nullable protected TileTool tileTool;
    protected float cameraSpeed = 5f;

    protected final Rectangle widgetAreaBounds = new Rectangle();
    protected final Rectangle scissorBounds = new Rectangle();

    public WorldWidget(@NotNull Tileset tileset, @NotNull TileLayerSource tileLayerSource, @NotNull OperationExecutor operationExecutor) {
        this.tileset = tileset;
        this.tileLayerSource = tileLayerSource;
        this.operationExecutor = operationExecutor;
        keyInputListener = new KeyInputListener(Keys.LEFT, Keys.UP, Keys.RIGHT, Keys.DOWN, Keys.ESCAPE) {
            @Override public void onInputDown(int keyCode) {
                switch (keyCode) {
                    case Keys.ESCAPE:
                        if (tileTool != null) tileTool.cancel();
                        break;
                }
            }
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
        addListener(this);
    }

    @Nullable public WorldDef getWorldDef() { return worldDef; }
    @NotNull public Vector2 getCameraPos() { return cameraPos; }
    @Nullable public TileTool getTileTool() { return tileTool; }
    public void setTileTool(@Nullable TileTool tileTool) {
        if (this.tileTool != null && tileTool != null && worldDef != null) {
            tileTool.transferState(this.tileTool, worldDef, tileLayerSource.getTileLayerIndex());
        }
        this.tileTool = tileTool;
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
                        getX() - Dst.getAsPixels(cameraPos.x),
                        getY() - Dst.getAsPixels(cameraPos.y),
                        tileset.getPixelsFromTile(worldDef.width),
                        tileset.getPixelsFromTile(worldDef.height),
                        1
                );

                Vector2 offsetPos = V2.get(cameraPos).sub(Dst.getAsMeters(getX()), Dst.getAsMeters(getY()));
                for (TileLayer tileLayer : tileLayers) {
                    for (TileLayer.Row row : tileLayer.getRows()) {
                        row.render(batch, offsetPos);
                    }
                }
                V2.claim(offsetPos);

                if (tileTool != null) {
                    tileTool.draw(this, batch);
                }
            }

            batch.flush();
            ScissorStack.popScissors();
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        keyInputListener.updateInputs(delta);
        if (tileTool != null && worldDef != null) {
            tileTool.update(worldDef, tileLayerSource.getTileLayerIndex());
        }
        if (tileLayers != null) for (TileLayer tileLayer : tileLayers) tileLayer.update(delta);
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof InputEvent) {
            InputEvent iEv = (InputEvent)event;
            if (tileTool != null) {
                switch (iEv.getType()) {
                    case mouseMoved:
                    case touchDragged:
                        Vector2 tmp = V2.get(iEv.getStageX() - getX(), iEv.getStageY() - getY());
                        tileTool.getPosMeters().set(Dst.getAsMeters(tmp).add(cameraPos));
                        V2.claim(tmp);
                        return true;
                    case touchDown:
                        if (worldDef != null) tileTool.begin(worldDef, tileLayerSource.getTileLayerIndex());
                        return true;
                    case touchUp:
                        if (worldDef != null) tileTool.complete(operationExecutor);
                        return true;
                }
            }
        }
        return false;
    }

    public interface TileLayerSource {
        int getTileLayerIndex();
    }
}
