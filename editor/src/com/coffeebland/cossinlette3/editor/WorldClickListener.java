package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.entity.TileLayer;
import com.coffeebland.cossinlette3.utils.VPool;

public abstract class WorldClickListener extends ClickListener {

    protected EditorState editorState;

    public WorldClickListener(EditorState editorState, int button) {
        super(button);
        this.editorState = editorState;
    }

    @Override public void clicked(InputEvent event, float x, float y) {
        TileLayer tileLayer = editorState.tileLayerChooser.getTileLayer();
        if (tileLayer == null) return;

        Vector2 tilePos = editorState.getTiledCoordinates(VPool.V2(Gdx.input.getX(), Gdx.input.getY()));
        int tileX = (int)tilePos.x;
        int tileY = (int)tilePos.y;
        VPool.claim(tilePos);

        if (tileX < 0 || tileX >= tileLayer.getWidth()
                || tileY < 0 || tileY >= tileLayer.getHeight()) {
            return;
        }

        int[] current = tileLayer.getTile(tileX, tileY);
        handleClick(tileLayer, tileX, tileY, current);
    }

    public abstract void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current);
}
