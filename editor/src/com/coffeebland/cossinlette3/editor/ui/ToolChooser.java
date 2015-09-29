package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.coffeebland.cossinlette3.editor.tools.Tool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Guillaume on 2015-09-22.
 */
public class ToolChooser extends HorizontalGroup {

    @NotNull protected Skin skin;
    @Nullable protected ToolSource toolSource;
    @NotNull protected ButtonGroup<ImageButton> btns = new ButtonGroup<>();
    @NotNull protected TextureRegion[] regions;

    public ToolChooser(@NotNull Skin skin) {
        this.skin = skin;
        TextureAtlas.AtlasRegion toolsRegion = skin.getAtlas().findRegion("tools");
        int size = toolsRegion.getRegionHeight();
        regions = toolsRegion.split(size, size)[0];
    }

    public void setSource(@NotNull ToolSource toolSource) {
        clearChildren();
        btns.clear();

        this.toolSource = toolSource;
        List<Tool> tools = toolSource.getTools();
        for (int i = 0, n = tools.size(); i < n; i++) {
            final int index = i;
            ImageButton imgBtn = new ImageButton(skin, "toggle");
            imgBtn.pad(2);
            imgBtn.setStyle(new ImageButton.ImageButtonStyle(imgBtn.getStyle()));
            imgBtn.getStyle().imageUp = new TextureRegionDrawable(regions[i]);
            imgBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    toolSource.setToolIndex(index);
                }
            });
            addActor(imgBtn);
            btns.add(imgBtn);
            if (i == toolSource.getToolIndex()) imgBtn.setChecked(true);
        }
        toolSource.listen((index) -> btns.getButtons().get(index).setChecked(true));
    }

    public interface ToolSource {
        @NotNull List<Tool> getTools();
        int getToolIndex();
        void setToolIndex(int index);
        void listen(ToolSourceListener listener);
    }
    public interface ToolSourceListener {
        void onIndexChanged(int index);
    }
}
