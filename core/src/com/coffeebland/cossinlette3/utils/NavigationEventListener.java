package com.coffeebland.cossinlette3.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class NavigationEventListener extends ClickListener {

    Button button, toTop, toLeft, toBottom, toRight;

    public NavigationEventListener(Button button) {
        this.button = toTop = toLeft = toBottom = toRight = button;
    }
    public NavigationEventListener setToTop(Button button) {
        toTop = button;
        return this;
    }
    public NavigationEventListener setToBottom(Button button) {
        toBottom = button;
        return this;
    }
    public NavigationEventListener setToLeft(Button button) {
        toLeft = button;
        return this;
    }
    public NavigationEventListener setToRight(Button button) {
        toRight = button;
        return this;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        Button newButton = null;
        switch (event.getKeyCode()) {
            case Input.Keys.LEFT:
                newButton = toLeft;
                break;
            case Input.Keys.RIGHT:
                newButton = toRight;
                break;
            case Input.Keys.DOWN:
                newButton = toBottom;
                break;
            case Input.Keys.UP:
                newButton = toTop;
                break;
            case Input.Keys.ENTER:
            case Input.Keys.SPACE:
                Stage stage = button.getStage();
                if (stage.getKeyboardFocus() instanceof Button) {
                    ((Button)stage.getKeyboardFocus()).setChecked(false);
                }
                stage.setKeyboardFocus(button);
                button.setChecked(true);
                handle();
                break;
        }
        if (newButton != null) {
            button.setChecked(false);
            newButton.setChecked(true);
            event.getStage().setKeyboardFocus(newButton);
            return true;
        }

        return super.keyDown(event, keycode);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        Actor currentFocus = button.getStage().getKeyboardFocus();
        if (currentFocus instanceof Button) {
            ((Button) currentFocus).setChecked(false);
        }
        button.setChecked(true);
        event.getStage().setKeyboardFocus(button);
        handle();
    }

    public abstract void handle();
}
