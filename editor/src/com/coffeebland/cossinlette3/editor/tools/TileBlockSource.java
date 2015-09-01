package com.coffeebland.cossinlette3.editor.tools;

/**
 * Created by Guillaume on 2015-08-30.
 */
public interface TileBlockSource {
    int getType();
    int getTypeIndex();
    int getTileOffset(float offset);
}
