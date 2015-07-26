package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffeebland.cossinlette3.editor.ui.FileChooser;
import com.coffeebland.cossinlette3.editor.ui.TileChooser;
import com.coffeebland.cossinlette3.editor.ui.TileLayerChooser;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.file.WorldFile;
import com.coffeebland.cossinlette3.state.State;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.Textures;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.Tool;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditorState extends State<FileHandle> {

    protected final Map<Integer, Long> keycodes = new HashMap<>();

    protected InputMultiplexer multiplexer;

    protected WorldFile worldFile;
    protected GameWorld world;
    protected FileHandle fileHandle;

    protected Stage stage;
    protected Viewport viewport;
    protected Skin skin;
    protected TileLayerChooser tileLayerChooser;
    protected ScrollPane tileChooserScroller;
    protected TileChooser tileChooser;
    protected Widget gameWorldWidget;

    protected Tool currentTool;
    protected Tool addTool;
    protected Tool removeTool;
    protected Tool replaceTool;

    protected Vector2 cameraPos;
    protected float cameraSpeed;

    public EditorState() {
        cameraSpeed = 5f;

        stage = new Stage(viewport = new ScreenViewport());
        multiplexer = new InputMultiplexer(stage, this);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.top().left();

        HorizontalGroup topbar = new HorizontalGroup();
        Container<HorizontalGroup> topbarContainer = new Container<>(topbar).left().background(skin.getDrawable("default-round"));

        TextButton newBtn = new TextButton("Nouveau", skin);
        newBtn.pad(4);
        newBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                setWorldFile(new WorldFile());
            }
        });
        topbar.addActor(newBtn);

        TextButton loadBtn = new TextButton("Charger", skin);
        loadBtn.pad(4);
        loadBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                FileChooser
                        .createLoadDialog("Ouvrir une carte", skin, Gdx.files.local("worlds"))
                        .setFilter((file) -> file.isDirectory() || file.getName().endsWith(".json"))
                        .setResultListener((success, result) -> {
                            if (result.isDirectory()) return false;
                            if (success) setWorldFile(WorldFile.read(result));
                            return true;
                        })
                        .show(stage);
            }
        });
        topbar.addActor(loadBtn);

        TextButton saveBtn = new TextButton("Sauvegarder", skin);
        saveBtn.pad(4);
        saveBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                FileChooser.createSaveDialog("Sauvegarder la carte", skin, Gdx.files.local("worlds"))
                        .setFilter((file) -> file.isDirectory() || file.getName().endsWith(".json"))
                        .setResultListener((success, result) -> {
                            if (result.isDirectory()) return false;
                            if (success) saveWorldFile(result);
                            return true;
                        })
                        .show(stage);
            }
        });
        topbar.addActor(saveBtn);

        table.row();
        table.add(topbarContainer).colspan(3).expandX().fillX();

        Table tileTable = new Table(skin);

        tileLayerChooser = new TileLayerChooser(skin, () -> world);
        Container<TileLayerChooser> tileLayerChooserContainer = new Container<>(tileLayerChooser).left().background(skin.getDrawable("default-round"));
        tileTable.row();
        tileTable.add(tileLayerChooserContainer).fillX();

        tileChooser = new TileChooser();
        tileLayerChooser.setTileChooser(tileChooser);

        Table tileChooserContainer = new Table();
        tileChooserContainer.row();
        tileChooserContainer.add(tileChooser).expandY().fill();

        tileChooserScroller = new ScrollPane(tileChooserContainer, skin);
        tileChooserScroller.setForceScroll(false, true);
        tileChooserScroller.setFadeScrollBars(false);
        tileTable.row();
        tileTable.add(tileChooserScroller).expandY().fill();

        table.row().expandY().fill();
        table.add(tileTable);

        gameWorldWidget = new Widget();
        gameWorldWidget.addListener(new WorldClickListener(this, Input.Buttons.LEFT) {
            @Override public void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current) {
                int[] newTiles = Arrays.copyOf(current, current.length + 2);
                newTiles[current.length] = tileChooser.getSelectedTileX();
                newTiles[current.length + 1] = tileChooser.getSelectedTileY();
                tileLayer.setTile(tileX, tileY, newTiles);
            }
        });
        gameWorldWidget.addListener(new WorldClickListener(this, Input.Buttons.RIGHT) {
            @Override public void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current) {
                if (current.length >= 2) {
                    tileLayer.setTile(tileX, tileY, Arrays.copyOf(current, current.length - 2));
                }
            }
        });
        table.add(gameWorldWidget).expandX();

        stage.addActor(table);
    }

    public void setWorldFile(WorldFile file) {
        if (world != null) world.dispose();
        if (cameraPos == null) cameraPos = VPool.V2();
        else cameraPos.setZero();

        worldFile = file;
        world = file.createGameWorld(null);
        tileLayerChooser.updateToTileLayers();
        tileChooser.invalidateHierarchy();
    }
    public void saveWorldFile(FileHandle fileHandle) {
        worldFile.write(fileHandle);
    }

    @Nullable @Override public InputProcessor getInputProcessor() { return multiplexer; }
    @Override public boolean shouldBeReused() { return false; }

    @Override public void onTransitionInStart(boolean firstTransition, @Nullable FileHandle fileHandle) {
        this.fileHandle = fileHandle;
        setWorldFile(worldFile = fileHandle != null ? WorldFile.read(fileHandle) : new WorldFile());
    }

    @Override public void onTransitionOutFinish() {
        stage.dispose();
        world.dispose();
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
        world.resize(width, height);
    }

    public void renderTileCursor(SpriteBatch batch) {

        TileLayer tileLayer = tileLayerChooser.getTileLayer();
        if (tileLayer == null) return;

        Vector2 pos = getTiledCoordinates(VPool.V2(Gdx.input.getX(), Gdx.input.getY()));

        if (pos.x < 0 || pos.x >= tileLayer.getWidth()
                || pos.y < 0 || pos.y >= tileLayer.getHeight()) {
            VPool.claim(pos);
            return;
        }

        getScreenTiledCoordinates(pos);
        int tileSize = tileLayer.getTileSize();

        Textures.drawRect(batch, Color.BLACK, (int) pos.x - 1, (int) pos.y - 1, tileSize + 2, tileSize + 2, 1);
        Textures.drawRect(batch, Color.WHITE, (int) pos.x, (int) pos.y, tileSize, tileSize, 1);

        VPool.claim(pos);
    }
    public void renderTilelayerBounds(SpriteBatch batch) {
        TileLayer currentTileLayer = tileLayerChooser.getTileLayer();
        for (TileLayer tileLayer : tileLayerChooser.getTileLayers()) {
            Color color = tileLayer == currentTileLayer ? Color.WHITE : Color.GRAY;
            Textures.drawRect(batch, color,
                    (int)Dst.getAsPixels(tileLayer.getX() - cameraPos.x) + Gdx.graphics.getWidth() / 2,
                    (int)Dst.getAsPixels(tileLayer.getY() - cameraPos.y) + Gdx.graphics.getHeight() / 2,
                    (tileLayer.getWidth() * tileLayer.getTileSize()),
                    (tileLayer.getHeight() * tileLayer.getTileSize()),
                    1
            );
        }
    }
    @Override public void render(@NotNull SpriteBatch batch) {
        world.camera.setTo(cameraPos);
        world.render(batch);

        batch.begin();

        renderTilelayerBounds(batch);
        renderTileCursor(batch);

        batch.end();

        stage.draw();
    }
    @Override public void update(float delta) {
        world.update(delta);
        stage.act(delta);
        updateInput(delta);
    }

    public void updateInput(float delta) {
        for (Map.Entry<Integer, Long> pressedKey: keycodes.entrySet()) {
            int keyCode = pressedKey.getKey();
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
    }
    @Override public boolean keyDown(int keycode) {
        keycodes.put(keycode, System.currentTimeMillis());
        return true;
    }
    @Override public boolean keyUp(int keycode) {
        keycodes.remove(keycode);
        return true;
    }

    @NotNull public Vector2 getTiledCoordinates(@NotNull Vector2 pos, int tileSize, float metersX, float metersY) {
        float xShift = (metersX - cameraPos.x) / Const.METERS_PER_PIXEL;
        float yShift = (metersY - cameraPos.y) / Const.METERS_PER_PIXEL;

        pos.set(
                pos.x - Gdx.graphics.getWidth() / 2 - xShift,
                Gdx.graphics.getHeight() / 2 - pos.y - yShift
        );
        pos.set((int) Math.floor(pos.x / (float) tileSize), (int) Math.floor(pos.y / (float) tileSize));

        return pos;
    }
    @NotNull public Vector2 getTiledCoordinates(@NotNull Vector2 pos) {
        TileLayer tileLayer = tileLayerChooser.getTileLayer();
        if (tileLayer == null) return pos;

        return getTiledCoordinates(pos,
                tileLayer.getTileSize(),
                tileLayer.getX(), tileLayer.getY()
        );
    }
    @NotNull public Vector2 getScreenTiledCoordinates(@NotNull Vector2 tilePos) {
        TileLayer tileLayer = tileLayerChooser.getTileLayer();
        assert tileLayer != null;

        int tileSize = tileLayer.getTileSize();

        float xShift = (tileLayer.getX() - cameraPos.x) / Const.METERS_PER_PIXEL;
        float yShift = (tileLayer.getY() - cameraPos.y) / Const.METERS_PER_PIXEL;

        tilePos.scl(tileSize)
                .add(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2)
                .add(xShift, yShift);

        return tilePos;
    }
}
