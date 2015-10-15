package com.coffeebland.cossinlette3.editor.tools;

import com.coffeebland.cossinlette3.game.entity.Tileset;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Guillaume on 2015-08-30.
 */
public interface TileSource {
    @NtN Tileset getTileset();
    int getSelectedTileX();
    int getSelectedTileY();
    int getSelectedWidth();
    int getSelectedHeight();
    TileBlockSource getTileBlockSource();
}
