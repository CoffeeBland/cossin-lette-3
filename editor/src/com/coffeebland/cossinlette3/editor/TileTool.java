package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.editor.ui.ClickTool;
import org.jetbrains.annotations.NotNull;

public abstract class TileTool extends ClickTool {

    protected TileSizeSource tileSizeSource;

    public TileTool(EditorState state, TileSizeSource tileSizeSource) {
        super(state);
        this.tileSizeSource = tileSizeSource;
    }

    @NotNull @Override public Vector2 transformCoordinates(@NotNull Vector2 pos) {
        return state.getTiledCoordinates(pos, tileSizeSource.getTileSize(), tileSizeSource.getX(), tileSizeSource.getY());
    }

    public interface TileSizeSource {
        int getTileSize();
        float getX();
        float getY();
    }
}
