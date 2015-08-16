package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffeebland.cossinlette3.editor.ui.FileChooser;
import com.coffeebland.cossinlette3.editor.ui.TileChooser;
import com.coffeebland.cossinlette3.editor.ui.TileLayerChooser;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.state.State;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditorState extends State<FileHandle> {

    protected final Map<Integer, Long> keycodes = new HashMap<>();

    protected InputMultiplexer multiplexer;

    protected TextureAtlas atlas;
    protected WorldDef worldDef;
    protected Tileset tileset;
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

        skin = new Skin(Gdx.files.internal("img/editor/main.json"));
        setNewWorldDef(40, 30, "main");

        Table table = new Table(skin);
        table.setFillParent(true);
        table.top().left();

        HorizontalGroup topbar = new HorizontalGroup();
        Container<HorizontalGroup> topbarContainer = new Container<>(topbar).left().background(skin.getDrawable("default-round"));

        TextButton newBtn = new TextButton("Nouveau", skin);
        newBtn.pad(4);
        newBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                // TODO prompt for size
                setNewWorldDef(40, 40, "main");
            }
        });
        newBtn.getClickListener().clicked(null, 0, 0);
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
                            if (success) setWorldDef(WorldDef.read(result));
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

        tileLayerChooser = new TileLayerChooser(skin, worldDef);
        Container<TileLayerChooser> tileLayerChooserContainer = new Container<>(tileLayerChooser).left().background(skin.getDrawable("default-round"));
        tileTable.row();
        tileTable.add(tileLayerChooserContainer).fillX();

        tileChooser = new TileChooser(tileset);

        Table tileChooserContainer = new Table();
        tileChooserContainer.row();
        tileChooserContainer.add(tileChooser).expandY().fill();

        tileChooserScroller = new ScrollPane(tileChooserContainer, skin);
        tileChooserScroller.setForceScroll(false, true);
        tileChooserScroller.setFadeScrollBars(false);
        tileChooserScroller.setFlickScroll(false);
        tileTable.row();
        tileTable.add(tileChooserScroller).expandY().fill();

        table.row().expandY().fill();
        table.add(tileTable);

        gameWorldWidget = new Widget();
        gameWorldWidget.addListener(new WorldClickListener(this, Input.Buttons.LEFT) {
            @Override public void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current) {
                // TODO handle add
            }
        });
        gameWorldWidget.addListener(new WorldClickListener(this, Input.Buttons.RIGHT) {
            @Override public void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current) {
                // TODO handle remove
            }
        });
        table.add(gameWorldWidget).expandX();

        stage.addActor(table);
    }

    public void setNewWorldDef(int widthMeters, int heightMeters, @NotNull String imgSrc) {
        WorldDef def = new WorldDef();
        def.width = widthMeters;
        def.height = heightMeters;
        def.imgSrc = imgSrc;
        def.staticPolygons = new ArrayList<>();
        def.tileLayers = IntStream.range(0, 5).mapToObj(i -> new TileLayerDef(def, i)).collect(Collectors.toList());
        setWorldDef(def);
    }
    public void setWorldDef(WorldDef file) {
        if (cameraPos == null) cameraPos = VPool.V2();
        else cameraPos.setZero();

        worldDef = file;

        FileHandle atlasHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".atlas");
        atlas = new TextureAtlas(atlasHandle);

        FileHandle tilesetHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".tileset.json");
        TilesetDef tilesetDef = new Json().fromJson(TilesetDef.class, tilesetHandle);
        tileset = new Tileset(atlas, tilesetDef);

        if (tileLayerChooser != null) tileLayerChooser.updateToTileLayers();
        if (tileChooser != null) tileChooser.invalidateHierarchy();
    }
    public void saveWorldFile(FileHandle fileHandle) {
        worldDef.write(fileHandle);
    }

    @Nullable @Override public InputProcessor getInputProcessor() { return multiplexer; }
    @Override public boolean shouldBeReused() { return false; }

    @Override public void onTransitionInStart(boolean firstTransition, @Nullable FileHandle fileHandle) {
        this.fileHandle = fileHandle;
        setWorldDef(worldDef = fileHandle != null ? WorldDef.read(fileHandle) : worldDef);
    }

    @Override public void onTransitionOutFinish() {
        stage.dispose();
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void renderTileCursor(SpriteBatch batch) {
        /* TODO tilecursor
        Vector2 pos = getTiledCoordinates(VPool.V2(Gdx.input.getX(), Gdx.input.getY()));

        if (pos.x < 0 || pos.x >= worldDef.width
                || pos.y < 0 || pos.y >= worldDef.height) {
            VPool.claim(pos);
            return;
        }

        getScreenTiledCoordinates(pos);
        int tileSizePixels = tileset.getTileSizePixels();

        Textures.drawRect(batch, Color.BLACK, (int) pos.x - 1, (int) pos.y - 1, tileSizePixels + 2, tileSizePixels + 2, 1);
        Textures.drawRect(batch, Color.WHITE, (int) pos.x, (int) pos.y, tileSizePixels, tileSizePixels, 1);

        VPool.claim(pos);*/
    }
    public void renderTilelayerBounds(SpriteBatch batch) {
        /* TODO tilelayerbounds
        TileLayer currentTileLayer = tileLayerChooser.getTileLayer();
        for (TileLayer tileLayer : tileLayerChooser.getTileLayers()) {
            Color color = tileLayer == currentTileLayer ? Color.WHITE : Color.GRAY;
            Textures.drawRect(batch, color,
                    (int)Dst.getAsPixels(-cameraPos.x) + Gdx.graphics.getWidth() / 2,
                    (int)Dst.getAsPixels(-cameraPos.y) + Gdx.graphics.getHeight() / 2,
                    (int)Dst.getAsPixels(world.getWidth()),
                    (int)Dst.getAsPixels(world.getHeight()),
                    1
            );
        }*/
    }
    @Override public void render(@NotNull SpriteBatch batch) {

        batch.begin();

        renderTilelayerBounds(batch);
        renderTileCursor(batch);

        batch.end();

        stage.draw();
    }
    @Override public void update(float delta) {
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

    /*@NotNull public Vector2 getTiledCoordinates(@NotNull Vector2 pos) {
        float xShift = -cameraPos.x / Const.METERS_PER_PIXEL;
        float yShift = -cameraPos.y / Const.METERS_PER_PIXEL;

        pos.set(
                pos.x - Gdx.graphics.getWidth() / 2 - xShift,
                Gdx.graphics.getHeight() / 2 - pos.y - yShift
        );
        pos.set((int) Math.floor(pos.x / tileset.getTileSizeMeters()), (int) Math.floor(pos.y / tileset.getTileSizeMeters()));

        return pos;
    }
    @NotNull public Vector2 getTiledCoordinates(@NotNull Vector2 pos) {
        TileLayer tileLayer = tileLayerChooser.getTileLayer();
        if (tileLayer == null) return pos;

        return getTiledCoordinates(pos, tileLayer.getTileset().getTileSizeMeters());
    }
    @NotNull public Vector2 getScreenTiledCoordinates(@NotNull Vector2 tilePos) {
        TileLayer tileLayer = tileLayerChooser.getTileLayer();
        assert tileLayer != null;

        int tileSizePixels = (int)tileLayer.getTileset().getTileSizePixels();

        float xShift = -cameraPos.x / Const.METERS_PER_PIXEL;
        float yShift = - cameraPos.y / Const.METERS_PER_PIXEL;

        tilePos.scl(tileSizePixels)
                .add(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2)
                .add(xShift, yShift);

        return tilePos;
    }*/
}
