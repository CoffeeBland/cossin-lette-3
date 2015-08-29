package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.coffeebland.cossinlette3.editor.ui.WorldWidget;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditorState extends State<FileHandle> {

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
    protected WorldWidget worldWidget;

    protected Tool currentTool;
    protected Tool addTool;
    protected Tool removeTool;
    protected Tool replaceTool;

    public EditorState() {
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

        worldWidget = new WorldWidget(tileset);
        table.add(worldWidget).expandX();

        stage.addActor(table);
        stage.setKeyboardFocus(worldWidget);
    }

    public void setNewWorldDef(int widthMeters, int heightMeters, @NotNull String imgSrc) {
        WorldDef def = new WorldDef();
        def.width = widthMeters;
        def.height = heightMeters;
        def.imgSrc = imgSrc;
        def.staticPolygons = new ArrayList<>();
        def.tileLayers = IntStream.range(0, 5).mapToObj(i -> new TileLayerDef(def, i)).collect(Collectors.toList());
        // TODO: don't add unused tiles
        def.tileLayers.get(0).addTile(0, 0, TileLayer.TYPE_STILL, 0, 0, 2);
        def.tileLayers.get(1).addTile(0, 0, TileLayer.TYPE_STILL, 0, 0, 3);
        setWorldDef(def);
    }
    public void setWorldDef(WorldDef file) {
        worldDef = file;

        FileHandle atlasHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".atlas");
        atlas = new TextureAtlas(atlasHandle);

        FileHandle tilesetHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".tileset.json");
        TilesetDef tilesetDef = new Json().fromJson(TilesetDef.class, tilesetHandle);
        tileset = new Tileset(atlas, tilesetDef);

        if (tileLayerChooser != null) tileLayerChooser.updateToTileLayers();
        if (tileChooser != null) tileChooser.invalidateHierarchy();
        if (worldWidget != null) worldWidget.resetToWorldDef(worldDef);
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

    @Override public void render(@NotNull SpriteBatch batch) {
        stage.draw();
    }
    @Override public void update(float delta) {
        stage.act(delta);
    }

    @Override public boolean keyDown(int keycode) {
        return true;
    }
    @Override public boolean keyUp(int keycode) {
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
