package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.tools.*;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.ActorDef;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.PolygonDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.game.visual.Charset;
import com.coffeebland.cossinlette3.editor.input.KeyInput;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WorldWidget
        extends Widget
        implements EventListener, ToolChooser.ToolSource {

    protected boolean displayOpaque = false;
    protected Color rowColor = new Color(0.75f, 0.75f, 0.75f, 1);

    @NtN protected CharsetAtlas charsetAtlas;
    @NtN protected Tileset tileset;
    @NtN protected TileLayerSource tileLayerSource;
    @NtN protected OperationExecutor operationExecutor;
    @N protected WorldDef worldDef;
    @NtN protected Map<ActorDef, Charset> charsets = new HashMap<>();
    @N protected List<TileLayer> tileLayers;
    @NtN protected Vector2 cameraPos = V2.get();
    @NtN protected KeyInput keyInputListener;
    @N protected ToolChooser.ToolSourceListener toolListener;

    protected List<Tool> tools;
    protected int currentToolIndex;
    protected float cameraSpeed = 5f;
    protected float[] tmpPoints;

    @NtN protected final Rectangle widgetAreaBounds = new Rectangle();
    @NtN protected final Rectangle scissorBounds = new Rectangle();
    @NtN protected ShapeRenderer shapeRenderer = new ShapeRenderer();
    @NtN protected Color polygonsBG = new Color(1/2, 0, 1/2, 1), polygonsFG = new Color(1, 1/2, 1, 1);

    public WorldWidget(
            @NtN Stage stage, @NtN Skin skin,
            @NtN CharsetAtlas charsetAtlas,
            @NtN Tileset tileset,
            @NtN TileLayerSource tileLayerSource,
            @NtN TileSource tileSource,
            @NtN OperationExecutor operationExecutor) {
        this.charsetAtlas = charsetAtlas;
        this.tileset = tileset;
        this.tileLayerSource = tileLayerSource;
        this.operationExecutor = operationExecutor;
        tools = Arrays.asList(
                new AddTool(tileSource, tileLayerSource),
                new RemoveTool(tileSource, tileLayerSource),
                new SetTool(tileSource, tileLayerSource),
                new ClearTool(tileSource, tileLayerSource),
                new AddPolygonTool(tileSource),
                new RemovePolygonTool(),
                new ActorTool(stage, skin)
        );
        currentToolIndex = 0;
        keyInputListener = new KeyInput(
                Keys.LEFT, Keys.UP, Keys.RIGHT, Keys.DOWN,
                Keys.ESCAPE,
                Keys.APOSTROPHE) {
            @Override public void onInputDown(int keyCode) {
                switch (keyCode) {
                    case Keys.ESCAPE:
                        tools.get(currentToolIndex).cancel();
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

    @N public WorldDef getWorldDef() { return worldDef; }
    @NtN public Vector2 getCameraPos() { return cameraPos; }
    public void setDisplayOpaque(boolean opaque) { displayOpaque = opaque; }
    public boolean isDisplayOpaque() { return displayOpaque; }

    @Override @NtN public List<Tool> getTools() {
        return tools;
    }
    @Override public int getToolIndex() {
        return currentToolIndex;
    }
    @Override public void setToolIndex(int index) {
        if (worldDef != null && index < tools.size()) {
            Tool current = tools.get(currentToolIndex);
            currentToolIndex = index;
            Tool nextTool = tools.get(currentToolIndex);
            nextTool.transferState(current, worldDef);
            if (toolListener != null) toolListener.onIndexChanged(currentToolIndex);
        }
    }
    @Override public void listen(ToolChooser.ToolSourceListener listener) {
        toolListener = listener;
    }

    public void resetToWorldDef(@N WorldDef def) {
        worldDef = def;
        charsets.clear();
        cameraPos.setZero();

        if (worldDef != null) {
            tileLayers = new ArrayList<>(worldDef.tileLayers.size());
            tileLayers.addAll(worldDef.tileLayers.stream().map(tileLayerDef -> new TileLayer(tileLayerDef, tileset)).collect(Collectors.toList()));
        } else {
            tileLayers = null;
        }
    }

    protected void drawBackground(@NtN Batch batch) {
        assert worldDef != null;

        Textures.drawFilledRect(batch,
                worldDef.backgroundColor,
                getX(), getY(),
                getWidth(), getHeight()
        );
    }
    protected void drawBounds(@NtN Batch batch) {
        assert worldDef != null;

        Textures.drawRect(batch,
                Color.WHITE,
                getX() - Dst.getAsPixels(cameraPos.x),
                getY() - Dst.getAsPixels(cameraPos.y),
                Dst.getAsPixels(worldDef.width),
                Dst.getAsPixels(worldDef.height),
                1
        );
    }
    protected void drawPolygons(@NtN Batch batch, @NtN Vector2 offsetPos) {
        assert worldDef != null;

        if (isDisplayOpaque()) return;

        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        worldDef.staticPolygons.stream().forEach(def -> drawPolygon(offsetPos, def, polygonsBG, 2));
        worldDef.staticPolygons.stream().forEach(def -> drawPolygon(offsetPos, def, polygonsFG, 1));
        shapeRenderer.end();
        batch.begin();
    }
    protected void drawPolygon(@NtN Vector2 offsetPos, @NtN PolygonDef def, Color color, float width) {
        if (tmpPoints == null) tmpPoints = new float[def.points.length];
        tmpPoints = def.getPixelPoints(tmpPoints, offsetPos);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(tmpPoints[0], tmpPoints[1], width);

        for (int i = 0; i < tmpPoints.length - 2; i += 2) {
            Vector2 diagonal = V2.get(
                    -(tmpPoints[i + 3] - tmpPoints[i + 1]),
                    tmpPoints[i + 2] - tmpPoints[i]
            ).nor().scl(width);

            shapeRenderer.setColor(color);
            shapeRenderer.circle(tmpPoints[i + 2], tmpPoints[i + 3], width);

            shapeRenderer.setColor(color);
            shapeRenderer.triangle(
                    tmpPoints[i] - diagonal.x, tmpPoints[i + 1] - diagonal.y,
                    tmpPoints[i + 2] - diagonal.x, tmpPoints[i + 3] - diagonal.y,
                    tmpPoints[i + 2] + diagonal.x, tmpPoints[i + 3] + diagonal.y
            );
            shapeRenderer.triangle(
                    tmpPoints[i] - diagonal.x, tmpPoints[i + 1] - diagonal.y,
                    tmpPoints[i] + diagonal.x, tmpPoints[i + 1] + diagonal.y,
                    tmpPoints[i + 2] + diagonal.x, tmpPoints[i + 3] + diagonal.y
            );

            V2.claim(diagonal);
        }
    }
    protected void drawPeople(@NtN Batch batch, @NtN Vector2 offsetPos) {
        assert worldDef != null;

        Collections.sort(worldDef.people, (lhs, rhs) -> Float.compare(rhs.y, lhs.y));

        if (!isDisplayOpaque()) {
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            worldDef.people.stream().forEach(def -> drawPerson(offsetPos, def, polygonsFG));
            shapeRenderer.end();
            batch.begin();
        }
        if (isDisplayOpaque() || tools.get(currentToolIndex) instanceof ActorTool) {
            batch.setColor(Color.WHITE);
        } else {
            rowColor.a = 1/3f;
            batch.setColor(rowColor);
        }
        worldDef.people.stream().forEach(def -> drawPersonCharset(batch, offsetPos, def));
        Color.WHITE.a = 1;
        batch.setColor(Color.WHITE);
    }
    protected void drawPerson(@NtN Vector2 offsetPos, @NtN PersonDef def, Color color) {
        Vector2 pixelPos = Dst.getAsPixels(V2.get(def.x, def.y).sub(offsetPos));
        shapeRenderer.setColor(color);
        shapeRenderer.circle(pixelPos.x, pixelPos.y, Dst.getAsPixels(def.radius));
        V2.claim(pixelPos);
    }
    protected void drawPersonCharset(@NtN Batch batch, @NtN Vector2 offsetPos, @NtN PersonDef def) {
        Vector2 pixelPos = Dst.getAsPixels(V2.get(def.x, def.y).sub(offsetPos));

        if (def.hasCharset()) {
            assert def.charset != null;
            if (!charsets.containsKey(def)) {
                Charset charset = charsetAtlas.getCharset(def.charset);
                charset.resolve(new BitSet());
                charsets.put(def, charset);
            }
            charsets.get(def).render(batch, pixelPos, def.orientation, 1);
        }

        V2.claim(pixelPos);
    }
    protected void drawTiles(@NtN Batch batch, @NtN Vector2 offsetPos) {
        assert tileLayers != null;

        for (int i = 0; i < tileLayers.size(); i++) {
            TileLayer tileLayer = tileLayers.get(i);
            if (displayOpaque || i == tileLayerSource.getTileLayerIndex()) {
                batch.setColor(Color.WHITE);
            } else {
                rowColor.a = i < tileLayerSource.getTileLayerIndex() ? 2f/3f : 1f/3f;
                batch.setColor(rowColor);
            }
            for (TileLayer.Row row : tileLayer.getRows()) {
                row.render(batch, offsetPos, getX(), getWidth(), getY(), getHeight());
            }
        }
        batch.setColor(Color.WHITE);
    }
    @Override public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (worldDef == null || tileLayers == null) return;

        widgetAreaBounds.set(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.setProjectionMatrix(
                shapeRenderer.getProjectionMatrix().setToOrtho2D(
                        0, 0,
                        Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
                )
        );

        getStage().calculateScissors(widgetAreaBounds, scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {
            Vector2 offsetPos = V2.get(cameraPos).sub(Dst.getAsMeters(getX()), Dst.getAsMeters(getY()));

            drawBackground(batch);
            drawBounds(batch);
            drawTiles(batch, offsetPos);
            drawPolygons(batch, offsetPos);
            drawPeople(batch, offsetPos);
            tools.get(currentToolIndex).draw(this, batch);

            V2.claim(offsetPos);

            batch.flush();
            ScissorStack.popScissors();
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        keyInputListener.update(delta);
        if (worldDef != null) {
            tools.get(currentToolIndex).update(worldDef);
        }
        if (tileLayers != null) tileLayers.stream().forEach(tl -> tl.update(delta));
        charsets.entrySet().stream().forEach(e -> e.getValue().update(delta));
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
