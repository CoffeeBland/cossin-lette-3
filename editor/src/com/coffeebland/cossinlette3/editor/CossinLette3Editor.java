package com.coffeebland.cossinlette3.editor;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.coffeebland.cossinlette3.CossinLette3;

public class CossinLette3Editor extends CossinLette3 {
    public CossinLette3Editor() {
        initialState = EditorState.class;
        try {
            TexturePacker.process("ui/uiskin/", "ui/", "uiskin");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
