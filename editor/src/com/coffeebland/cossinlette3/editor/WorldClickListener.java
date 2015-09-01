package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.coffeebland.cossinlette3.game.entity.TileLayer;

public abstract class WorldClickListener extends ClickListener {

    protected EditorState editorState;

    public WorldClickListener(EditorState editorState, int button) {
        super(button);
        this.editorState = editorState;
    }

    @Override public void clicked(InputEvent event, float x, float y) {
        /* TODO worldClickListener
        GameWorld world = editorState.world;
        TileLayer tileLayer = editorState.tileLayerChooser.getTileLayer();
        if (tileLayer == null) return;

        Vector2 tilePos = editorState.getTiledCoordinates(get.get(Gdx.input.getX(), Gdx.input.getY()));
        int tileX = (int)tilePos.x;
        int tileY = (int)tilePos.y;
        get.claim(tilePos);

        if (tileX < 0 || tileX >= world.getWidth()
                || tileY < 0 || tileY >= world.getHeight()) {
            return;
        }

        int[] current = tileLayer.getTile(tileX, tileY);
        handleClick(tileLayer, tileX, tileY, current);*/
    }

    public abstract void handleClick(TileLayer tileLayer, int tileX, int tileY, int[] current);
}
