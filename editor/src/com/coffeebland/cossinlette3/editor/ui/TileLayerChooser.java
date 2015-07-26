package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.GameWorld;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TileLayerChooser extends HorizontalGroup {

    @NotNull protected WorldSource source;
    @NotNull protected Skin skin;

    @NotNull protected TextButton addBtn;
    @NotNull protected TextButton editBtn;
    @NotNull protected TextButton deleteBtn;
    @NotNull protected ButtonGroup<TextButton> btns = new ButtonGroup<>();

    protected int selectedTileLayer;
    @NotNull protected List<TileLayer> tileLayers;

    @Nullable protected TileChooser tileChooser;

    public TileLayerChooser(@NotNull Skin skin, @NotNull WorldSource source) {
        this.source = source;
        tileLayers = new ArrayList<>();

        editBtn = new TextButton(" # ", skin);
        editBtn.pad(0, 4, 0, 4);
        editBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                updateToTileLayers();
            }
        });

        addBtn = new TextButton(" + ", skin);
        addBtn.pad(0, 4, 0, 4);
        addBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                updateToTileLayers();
            }
        });

        deleteBtn = new TextButton(" - ", skin);
        deleteBtn.pad(0, 4, 0, 4);
        deleteBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                updateToTileLayers();
            }
        });

        this.skin = skin;
    }

    public TileLayerChooser setTileChooser(@Nullable TileChooser tileChooser) {
        this.tileChooser = tileChooser;

        if (tileChooser != null && tileLayers.size() > 0) {
            tileChooser.setTileLayer(tileLayers.get(selectedTileLayer));
        }

        return this;
    }
    @NotNull public List<TileLayer> getTileLayers() {
        return tileLayers;
    }
    @Nullable public TileLayer getTileLayer() {
        return tileLayers.size() > 0 ? tileLayers.get(selectedTileLayer) : null;
    }

    public void updateToTileLayers() {
        tileLayers.clear();
        clearChildren();
        btns.clear();
        tileLayers.addAll(source.getWorld().actors.stream()
                .filter(actor -> actor instanceof TileLayer)
                .map(actor -> (TileLayer) actor)
                .collect(Collectors.toList())
        );

        selectedTileLayer = 0;
        for (int i = 0; i < tileLayers.size(); i++) {
            TextButton tileBtn = new TextButton(" " + Integer.toString(i) + " ", skin, "toggle");
            tileBtn.pad(0, 4, 0, 4);
            final int tileLayer = i;
            tileBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    selectedTileLayer = tileLayer;
                    if (tileChooser != null) tileChooser.setTileLayer(tileLayers.get(selectedTileLayer));
                }
            });
            addActor(tileBtn);
            btns.add(tileBtn);
            if (i == selectedTileLayer) tileBtn.setChecked(true);
        }

        if (tileChooser != null) {
            tileChooser.setTileLayer(tileLayers.size() > 0 ? tileLayers.get(selectedTileLayer) : null);
        }

        addActor(editBtn);
        addActor(addBtn);
        addActor(deleteBtn);
    }

    public interface WorldSource {
        GameWorld getWorld();
    }
}
