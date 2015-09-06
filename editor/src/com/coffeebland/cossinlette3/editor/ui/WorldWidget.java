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
import com.coffeebland.cossinlette3.editor.tools.*;
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
import java.util.Arrays;
import java.util.List;

public class WorldWidget extends Widget implements EventListener {

    protected boolean displayOpaque = false;
    protected Color rowColor = new Color(0.75f, 0.75f, 0.75f, 1);

    @NotNull protected Tileset tileset;
    @NotNull protected TileLayerSource tileLayerSource;
    @NotNull protected OperationExecutor operationExecutor;
    @Nullable protected WorldDef worldDef;
    @Nullable protected List<TileLayer> tileLayers;
    @NotNull protected Vector2 cameraPos = V2.get();
    @NotNull protected KeyInputListener keyInputListener;

    protected List<Tool> tools;
    protected int currentToolIndex;
    protected float cameraSpeed = 5f;

    protected final Rectangle widgetAreaBounds = new Rectangle();
    protected final Rectangle scissorBounds = new Rectangle();

    public WorldWidget(@NotNull Tileset tileset, @NotNull TileLayerSource tileLayerSource, @NotNull TileSource tileSource, @NotNull OperationExecutor operationExecutor) {
        this.tileset = tileset;
        this.tileLayerSource = tileLayerSource;
        this.operationExecutor = operationExecutor;
        tools = Arrays.asList(
                new AddTool(tileSource, tileLayerSource),
                new RemoveTool(tileSource, tileLayerSource),
                new SetTool(tileSource, tileLayerSource),
                new ClearTool(tileSource, tileLayerSource)
        );
        currentToolIndex = 0;
        keyInputListener = new KeyInputListener(
                Keys.LEFT, Keys.UP, Keys.RIGHT, Keys.DOWN,
                Keys.ESCAPE,
                Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT,
                Keys.APOSTROPHE) {
            @Override public void onInputDown(int keyCode) {
                switch (keyCode) {
                    case Keys.ESCAPE:
                        tools.get(currentToolIndex).cancel();
                        break;
                    case Keys.CONTROL_LEFT:
                    case Keys.CONTROL_RIGHT:
                        if (worldDef != null) {
                            Tool current = tools.get(currentToolIndex);
                            currentToolIndex = (currentToolIndex + 1) % tools.size();
                            Tool nextTool = tools.get(currentToolIndex);
                            nextTool.transferState(current, worldDef);
                        }
                        break;
                    case Keys.APOSTROPHE:
                        setDisplayOpaque(!isDisplayOpaque());
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
    public void setDisplayOpaque(boolean opaque) { displayOpaque = opaque; }
    public boolean isDisplayOpaque() { return displayOpaque; }

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
                for (int i = 0; i < tileLayers.size(); i++) {
                    TileLayer tileLayer = tileLayers.get(i);
                    if (displayOpaque || i == tileLayerSource.getTileLayerIndex()) {
                        batch.setColor(Color.WHITE);
                    } else {
                        rowColor.a = i < tileLayerSource.getTileLayerIndex() ? 2f/3f : 1f/3f;
                        batch.setColor(rowColor);
                    }
                    for (TileLayer.Row row : tileLayer.getRows()) {
                        row.render(batch, offsetPos);
                    }
                }
                V2.claim(offsetPos);

                tools.get(currentToolIndex).draw(this, batch);
            }

            batch.flush();
            ScissorStack.popScissors();
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        keyInputListener.updateInputs(delta);
        if (worldDef != null) {
            tools.get(currentToolIndex).update(worldDef);
        }
        if (tileLayers != null) for (TileLayer tileLayer : tileLayers) tileLayer.update(delta);
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof InputEvent) {
            InputEvent iEv = (InputEvent)event;
            Tool current = tools.get(currentToolIndex);
            switch (iEv.getType()) {
                case mouseMoved:
                case touchDragged:
                    Vector2 tmp = V2.get(iEv.getStageX() - getX(), iEv.getStageY() - getY());
                    current.getPosMeters().set(Dst.getAsMeters(tmp).add(cameraPos));
                    V2.claim(tmp);
                    return true;
                case touchDown:
                    if (worldDef != null) current.begin(worldDef);
                    return true;
                case touchUp:
                    if (worldDef != null) current.complete(operationExecutor);
                    return true;
            }
        }
        return false;
    }

}
