package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffeebland.cossinlette3.editor.ui.*;
import com.coffeebland.cossinlette3.game.entity.Tileset;
import com.coffeebland.cossinlette3.game.file.TilesetDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.state.StateImpl;
import com.coffeebland.cossinlette3.state.StateManager;
import com.coffeebland.cossinlette3.utils.CharsetAtlas;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorState extends StateImpl<FileHandle> implements OperationExecutor {

    protected InputMultiplexer multiplexer;

    protected TextureAtlas atlas;
    protected CharsetAtlas charAtlas;
    protected WorldDef worldDef;
    protected Tileset tileset;
    protected FileHandle fileHandle;

    protected Stage stage;
    protected Viewport viewport;
    protected Skin skin;

    protected ToolChooser toolChooser;
    protected TileLayerChooser tileLayerChooser;
    protected ScrollPane tileChooserScroller;
    protected TileChooser tileChooser;
    protected WorldWidget worldWidget;

    protected List<Operation> operations = new ArrayList<>();
    protected int operationIndex;

    public EditorState() {
        stage = new Stage(viewport = new ScreenViewport());
        multiplexer = new InputMultiplexer(stage, this);

        FileHandle charsetHandle = Gdx.files.internal("img/game/charset.atlas");
        charAtlas = new CharsetAtlas(charsetHandle);
        skin = new Skin(Gdx.files.internal("img/editor/main.json"));
        setNewWorldDef(40, 30, 9, Color.BLACK.cpy(), "forest");

        Table table = new Table(skin);
        table.setFillParent(true);
        table.top().left();

        HorizontalGroup topbar = new HorizontalGroup();
        Container<HorizontalGroup> topbarContainer = new Container<>(topbar).left().background(skin.getDrawable("default-round"));

        TextButton newBtn = new TextButton("Nouveau", skin);
        newBtn.pad(4);
        newBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new OptionsChooser(
                        "Options de la carte", skin,
                        (w, h, ts, tLs, c) -> setNewWorldDef(w, h, tLs, c, ts)
                ).show(stage);
            }
        });
        newBtn.getClickListener().clicked(null, 0, 0);

        TextButton loadBtn = new TextButton("Charger", skin);
        loadBtn.pad(4);
        loadBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        TextButton saveBtn = new TextButton("Sauvegarder", skin);
        saveBtn.pad(4);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FileChooser
                        .createSaveDialog("Sauvegarder la carte", skin, Gdx.files.local("worlds"))
                        .setFilter((file) -> file.isDirectory() || file.getName().endsWith(".json"))
                        .setResultListener((success, result) -> {
                            if (result.isDirectory()) return false;
                            if (success) saveWorldFile(result);
                            return true;
                        })
                        .show(stage);
            }
        });

        TextButton editBtn = new TextButton("Options", skin);
        editBtn.pad(4);
        editBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new OptionsChooser("Options de la carte", skin, worldDef, (w, h, ts, tLs, c) -> {
                    worldDef.imgSrc = ts;
                    worldDef.resize(tileset, w, h, tLs);
                    worldDef.backgroundColor = c;
                    setWorldDef(worldDef);
                }).show(stage);
            }
        });

        toolChooser = new ToolChooser(skin) {
            @Override
            public float getPrefHeight() {
                return newBtn.getPrefHeight();
            }
        };

        topbar.addActor(newBtn);
        topbar.addActor(loadBtn);
        topbar.addActor(saveBtn);
        topbar.addActor(editBtn);
        topbar.addActor(toolChooser);

        Table tileTable = new Table(skin);

        tileLayerChooser = new TileLayerChooser(skin);
        Container<TileLayerChooser> tileLayerChooserContainer = new Container<>(tileLayerChooser).left().background(skin.getDrawable("default-round"));

        tileChooser = new TileChooser(tileset);

        Table tileChooserContainer = new Table();
        tileChooserContainer.row();
        tileChooserContainer.add(tileChooser).expandY().fill();

        tileChooserScroller = new ScrollPane(tileChooserContainer, skin);
        tileChooserScroller.setForceScroll(false, true);
        tileChooserScroller.setFadeScrollBars(false);
        tileChooserScroller.setFlickScroll(false);

        tileTable.row();
        tileTable.add(tileLayerChooserContainer).fillX();

        tileTable.row();
        tileTable.add(tileChooserScroller).expandY().fill();

        worldWidget = new WorldWidget(stage, skin, charAtlas, tileset, tileLayerChooser, tileChooser, this);
        toolChooser.setSource(worldWidget);

        table.row();
        table.add(topbarContainer).colspan(4).expandX().fillX();

        table.row().expandY().fill();
        table.add(tileTable).colspan(3);
        table.add(worldWidget).expandX();

        stage.addActor(table);
        stage.setKeyboardFocus(worldWidget);
    }

    public void setNewWorldDef(int widthMeters, int heightMeters, int tileLayersSize, Color color, @NtN String imgSrc) {
        WorldDef def = new WorldDef();
        def.width = widthMeters;
        def.height = heightMeters;
        def.backgroundColor = color;
        def.imgSrc = imgSrc;
        def.staticPolygons = new ArrayList<>();

        FileHandle atlasHandle = Gdx.files.internal("img/game/" + def.imgSrc + ".atlas");
        atlas = new TextureAtlas(atlasHandle);

        FileHandle tilesetHandle = Gdx.files.internal("img/game/" + def.imgSrc + ".tileset.json");
        TilesetDef tilesetDef = new Json().fromJson(TilesetDef.class, tilesetHandle);
        tileset = new Tileset(atlas, tilesetDef);

        def.resize(tileset, widthMeters, heightMeters, tileLayersSize);

        worldDef = def;
        if (tileLayerChooser != null) tileLayerChooser.updateToTileLayers(worldDef);
        if (tileChooser != null) tileChooser.invalidateHierarchy();
        if (worldWidget != null) worldWidget.resetToWorldDef(worldDef);
    }
    public void setWorldDef(WorldDef file) {
        worldDef = file;

        FileHandle atlasHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".atlas");
        atlas = new TextureAtlas(atlasHandle);

        FileHandle tilesetHandle = Gdx.files.internal("img/game/" + worldDef.imgSrc + ".tileset.json");
        TilesetDef tilesetDef = new Json().fromJson(TilesetDef.class, tilesetHandle);
        tileset = new Tileset(atlas, tilesetDef);

        if (tileLayerChooser != null) tileLayerChooser.updateToTileLayers(worldDef);
        if (tileChooser != null) tileChooser.invalidateHierarchy();
        if (worldWidget != null) worldWidget.resetToWorldDef(worldDef);
    }
    public void saveWorldFile(FileHandle fileHandle) {
        worldDef.write(fileHandle);
    }

    @Override public void execute(@NtN Operation operation, boolean runOp) {
        operations.subList(operationIndex, operations.size()).clear();
        if (runOp) operation.execute();
        operations.add(operation);
        operationIndex = operations.size();
    }
    @Override public void undo() {
        if (operationIndex > 0 && operationIndex <= operations.size()) {
            operations.get(operationIndex - 1).cancel();
            operationIndex--;
        }
    }
    @Override public void redo() {
        if (operationIndex < operations.size()) {
            operations.get(operationIndex).execute();
            operationIndex++;
        }
    }

    @Override @N public InputProcessor getInputProcessor() { return multiplexer; }

    @Override public void onPrepare(@N FileHandle fileHandle, StateManager.Notifier notifier) {
        super.onPrepare(fileHandle, notifier);

        this.fileHandle = fileHandle;
        setWorldDef(worldDef = fileHandle != null ? WorldDef.read(fileHandle) : worldDef);
        notifier.prepared();
    }
    @Override public void onTransitionOutFinish() {
        stage.dispose();
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void render(@NtN Batch batch) {
        stage.draw();
    }
    @Override public void update(float delta) {
        super.update(delta);
        stage.act(delta);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.Z:
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    undo();
                    return true;
                }
                break;
            case Input.Keys.Y:
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    redo();
                    return true;
                }
                break;
        }
        rangeKeysOver(Input.Keys.F1, Input.Keys.F12, keycode, tileLayerChooser::setTileLayerIndex);
        rangeKeysOver(Input.Keys.NUM_1, Input.Keys.NUM_9, keycode, worldWidget::setToolIndex);
        return super.keyDown(keycode);
    }
    protected void rangeKeysOver(int start, int end, int keycode, Consumer<Integer> func) {
        if (keycode >= start && keycode <= end) func.accept(keycode - start);
    }

}
