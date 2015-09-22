package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.file.TileLayerDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileLayerChooser extends HorizontalGroup implements TileLayerSource {

    @NotNull protected List<TileLayerDef> tileLayerDefs = new ArrayList<>();
    @NotNull protected Skin skin;

    @NotNull protected ButtonGroup<TextButton> btns = new ButtonGroup<>();

    protected int selectedTileLayer;

    public TileLayerChooser(@NotNull Skin skin) {
        this.skin = skin;
    }

    @NotNull public List<TileLayerDef> getTileLayers() {
        return tileLayerDefs;
    }
    @Nullable public TileLayerDef getTileLayer() {
        return tileLayerDefs.size() > 0 ? tileLayerDefs.get(selectedTileLayer) : null;
    }

    @Override
    public int getTileLayerIndex() {
        return selectedTileLayer;
    }
    public boolean setTileLayerIndex(int index) {
        if (index >= tileLayerDefs.size()) return false;
        btns.getButtons().get(selectedTileLayer = index).setChecked(true);
        return true;
    }

    public void updateToTileLayers(@NotNull WorldDef worldDef) {
        this.tileLayerDefs = worldDef.tileLayers;
        clearChildren();
        btns.clear();

        selectedTileLayer = 0;
        for (int i = 0; i < tileLayerDefs.size(); i++) {
            TextButton tileBtn = new TextButton(" " + Integer.toString(i + 1) + " ", skin, "toggle");
            tileBtn.pad(2);
            final int tileLayer = i;
            tileBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    selectedTileLayer = tileLayer;
                }
            });
            addActor(tileBtn);
            btns.add(tileBtn);
            if (i == selectedTileLayer) tileBtn.setChecked(true);
        }
    }
}
